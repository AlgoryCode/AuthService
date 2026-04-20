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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final TokenLogRepository tokenLogRepository;
    private final UserRepository userRepository;

    public TokenResponse generateToken(UserResponse user) {
        String uuid = UUID.randomUUID().toString();

        String accessToken = jwtUtil.generateAccessToken(user, uuid);
        RefreshTokenResponse refreshToken = jwtUtil.generateRefreshToken(uuid);



        tokenLogRepository.save( TokenLog.builder()
                .userId(user.getUserId())
                .sessionId(uuid)
                .expiryDate(refreshToken.getExpiryDate())
                .userAgent("")
                .refreshTokenId(refreshToken.getRefreshTokenId())
                .proccessStatus("")
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .userId(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getFamilyName())
                .build();
    }

    public void revokeToken(String refreshToken) {
        String sessionId = jwtUtil.extractSessionIdFromToken(refreshToken);
        if (sessionId == null || sessionId.isBlank()) {
            return;
        }
        TokenLog tokenLog = tokenLogRepository.findBySessionId(sessionId);
        if (tokenLog == null) {
            return;
        }
        tokenLog.setRevokedAt(LocalDateTime.now());
        tokenLog.setRevoked(true);
        tokenLogRepository.save(tokenLog);
    }

    public ResponseEntity<?> refreshToken(String reToken) {

        String sessionId = jwtUtil.extractSessionIdFromToken(reToken);
        if (sessionId == null || sessionId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
        TokenLog tokenLog = tokenLogRepository.findBySessionId(sessionId);
        if (tokenLog == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unknown refresh session");
        }

        if (tokenLog.getExpiryDate().isBefore(LocalDateTime.now()) || tokenLog.isRevoked()){
            if(!tokenLog.isRevoked()){
                tokenLog.setRevoked(true);
                tokenLog.setRevokedAt(LocalDateTime.now());
                tokenLogRepository.save(tokenLog);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
        }

        User user = userRepository
                .findByIdWithRoleAndAuthorities(tokenLog.getUserId())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        String uuid = UUID.randomUUID().toString();

        String accessToken = jwtUtil.generateAccessToken(userMapper.toResponse(user), uuid);
        RefreshTokenResponse refreshToken = jwtUtil.generateRefreshToken(uuid);

        tokenLog.setSessionId(uuid);
        tokenLog.setRefreshedAt(LocalDateTime.now());
        tokenLog.setExpiryDate(refreshToken.getExpiryDate());

        tokenLogRepository.save(tokenLog);

        UserResponse refreshedUser = userMapper.toResponse(user);
        return ResponseEntity.ok().body(TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .userId(refreshedUser.getUserId())
                .email(refreshedUser.getEmail())
                .firstName(refreshedUser.getFirstName())
                .lastName(refreshedUser.getFamilyName())
                .build());
    }

}
