package com.ael.authservice.controller;

import com.ael.authservice.dto.request.GoogleAuthRequest;
import com.ael.authservice.dto.response.TokenResponse;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.model.AuthType;
import com.ael.authservice.service.AuthService;
import com.ael.authservice.service.TokenService;
import com.ael.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/google-auth")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> googleLogin(@RequestBody String idToken) {

        UserResponse user = authService.authenticate(
                AuthType.GOOGLE,
                new GoogleAuthRequest(idToken)
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

    @PostMapping("/register")
    public ResponseEntity<?> googleRegister(@RequestBody GoogleAuthRequest request) {
        return ResponseEntity.ok("");
    }
}
