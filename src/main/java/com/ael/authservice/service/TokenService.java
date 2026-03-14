package com.ael.authservice.service;

import com.ael.authservice.dto.response.RefreshTokenResponse;
import com.ael.authservice.dto.response.TokenResponse;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.mapper.UserMapper;
import com.ael.authservice.model.TokenLog;
import com.ael.authservice.model.User;
import com.ael.authservice.repository.TokenLogRepository;
import com.ael.authservice.repository.UserRepository;
import com.ael.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private TokenLogRepository tokenLogRepository;
    private UserRepository userRepository;

    public TokenResponse generateToken(UserResponse user) {
        String uuid = UUID.randomUUID().toString();

        String accessToken = jwtUtil.generateAccessToken(user, uuid);
        RefreshTokenResponse refreshToken = jwtUtil.generateRefreshToken(uuid);

        TokenLog.builder()
                .userId(user.getUserId())
                .sessionId(uuid)
                .expiryDate(refreshToken.getExpiryDate())
                .userAgent("")
                .refreshTokenId(refreshToken.getRefreshTokenId())
                .proccessStatus("")
                .build();

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public void revokeToken(String refreshToken) {

        TokenLog tokenLog = tokenLogRepository.findBySessionId(jwtUtil.extractSessionIdFromToken(refreshToken));

        tokenLog.setRevokedAt(LocalDateTime.now());
        tokenLog.setRevoked(true);

        tokenLogRepository.save(tokenLog);
    }

    public TokenResponse refreshToken(String reToken) {

        Integer userId = jwtUtil.extractUserId(reToken);
        TokenLog tokenLog = tokenLogRepository.findTokenLogByUserId(userId);

        if (tokenLog.getExpiryDate().isBefore(LocalDateTime.now())){
            if(!tokenLog.isRevoked()){
                tokenLog.setRevoked(true);
                tokenLog.setRevokedAt(LocalDateTime.now());
                tokenLogRepository.save(tokenLog);
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        String uuid = UUID.randomUUID().toString();

        String accessToken = jwtUtil.generateAccessToken(userMapper.toResponse(user), uuid);
        RefreshTokenResponse refreshToken = jwtUtil.generateRefreshToken(uuid);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

}
