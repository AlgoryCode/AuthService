package com.ael.authservice.controller;

import com.ael.authservice.dto.request.BasicAuthRequest;
import com.ael.authservice.dto.request.GoogleAuthRequest;
import com.ael.authservice.dto.response.RefreshTokenResponse;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.mapper.UserMapper;
import com.ael.authservice.model.User;
import com.ael.authservice.service.AuthService;
import com.ael.authservice.service.AuthType;
import com.ael.authservice.service.GoogleAuthProvider;
import com.ael.authservice.service.UserService;
import com.ael.authservice.service.UserSessionLogService;
import com.ael.authservice.util.JwtUtil;
import com.ael.authservice.model.LoginRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;


@RestController
@RefreshScope
@RequestMapping("/basicauth")
@AllArgsConstructor
public class BasicAuthController {

    private static final Logger log = LoggerFactory.getLogger(BasicAuthController.class);

    private final JwtUtil jwtUtil;
    private final UserSessionLogService userSessionLogService;
    private final AuthService authService;
    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {


        UserResponse user = authService.authenticate(
                AuthType.BASIC,
                new BasicAuthRequest(req.getEmail(), req.getPassword())
        );


        String accessToken = jwtUtil.generateAccessToken(user);
        RefreshTokenResponse refresh = jwtUtil.generateRefreshToken();

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refresh.getRefreshToken()
        ));

//        userSessionLogService.createSessionLog(UserSessionLog.builder()
//                .refreshTokenId(refreshTokenResponse.getRefreshTokenId())
//                .accessTokenId(accessTokenResponse.getJti())
//                .sessionId(accessTokenResponse.getSession_id())
//                .userAgent("")
//                .build());
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue String access_token) {
        try {

            String sessionId = jwtUtil.extractSessionIdFromToken(access_token);
            Integer userId = jwtUtil.extractUserIdFromToken(access_token);

            userSessionLogService.revokeUserSession(sessionId, userId);

            return ResponseEntity.ok().body("Success");

        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(500).body("Logout failed");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.ok("User Created Successfully");
    }

    @PostMapping("/refreshAccessToken")
    public ResponseEntity<?> refreshAccessToken(@CookieValue String access_token) {

        String session_id = jwtUtil.extractSessionIdFromToken(access_token);
        Integer userId = jwtUtil.extractUserIdFromToken(access_token);
        if (userSessionLogService.getRefreshTokenBySessionId(session_id).isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token Expired");
        }
        return ResponseEntity.ok().body("");

    }

    @PostMapping("/google/login")
    public ResponseEntity<?> googleLogin(@RequestBody String idToken) {


        UserResponse user = authService.authenticate(
                AuthType.GOOGLE,
                new GoogleAuthRequest(idToken)
        );

        String accessToken = jwtUtil.generateAccessToken(user);
        RefreshTokenResponse refresh = jwtUtil.generateRefreshToken();

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refresh.getRefreshToken()
        ));

    }

    @PostMapping("/google/register")
    public ResponseEntity<?> googleRegister(@RequestBody GoogleAuthRequest request) {
        return ResponseEntity.ok("");
    }

    @PostMapping("/revoke-refreshToken")
    public ResponseEntity<String> revokeRefreshToken(@CookieValue String access_token) {
        String uuid = jwtUtil.extractSessionIdFromToken(access_token);
        return ResponseEntity.ok("Refresh token revoked");
    }

}





