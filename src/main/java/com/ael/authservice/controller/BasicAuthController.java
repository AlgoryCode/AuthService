package com.ael.authservice.controller;

import com.ael.authservice.dto.request.BasicAuthRequest;
import com.ael.authservice.dto.request.BasicRegisterRequest;
import com.ael.authservice.dto.request.GoogleAuthRequest;
import com.ael.authservice.dto.response.RefreshTokenResponse;
import com.ael.authservice.dto.response.TokenResponse;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.model.AuthType;
import com.ael.authservice.service.AuthService;
import com.ael.authservice.service.TokenService;
import com.ael.authservice.util.JwtUtil;
import com.ael.authservice.model.LoginRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;


@RestController
@RefreshScope
@RequestMapping("/basicauth")
@AllArgsConstructor
public class BasicAuthController {

    private static final Logger log = LoggerFactory.getLogger(BasicAuthController.class);

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {

        UserResponse user = authService.authenticate(
                AuthType.BASIC,
                new BasicAuthRequest(req.getEmail(), req.getPassword())
        );

        if (user.isTwoFactorEnabled()) {
            String pending = jwtUtil.generatePendingTwoFactorToken(user.getUserId());
            return ResponseEntity.ok(TokenResponse.builder()
                    .requiresTwoFactor(true)
                    .twoFactorToken(pending)
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getFamilyName())
                    .build());
        }

        return ResponseEntity.ok(tokenService.generateToken(user));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue String refresh_token) {
        tokenService.revokeToken(refresh_token);
        return ResponseEntity.ok().body("Logout out");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody BasicRegisterRequest request) {
        authService.register(AuthType.BASIC, request);
        return ResponseEntity.ok("User Created Successfully");
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshAccessToken(@CookieValue String refreshToken) {
        return tokenService.refreshToken(refreshToken);
    }

    @PostMapping("/revoke-refreshtoken")
    public ResponseEntity<String> revokeRefreshToken(@CookieValue String refresh_token) {
        tokenService.revokeToken(refresh_token);
        return ResponseEntity.ok("Refresh token revoked");
    }

}





