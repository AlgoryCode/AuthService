package com.ael.authservice.controller;

import com.ael.authservice.dto.request.TotpCodeRequest;
import com.ael.authservice.dto.response.TokenResponse;
import com.ael.authservice.dto.response.TwoFactorSetupResponse;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.service.TokenService;
import com.ael.authservice.service.TwoFactorService;
import com.ael.authservice.service.UserService;
import com.ael.authservice.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/2fa")
@RequiredArgsConstructor
public class TwoFactorController {

    private final TwoFactorService twoFactorService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final UserService userService;

    @PostMapping(value = "/enabled", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> enableQr(@RequestHeader("X-User-Id") String userIdHeader) {
        Integer userId = Integer.parseInt(userIdHeader.trim());
        byte[] png = twoFactorService.prepareSecretAndQrPng(userId);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(png);
    }

    /** Kurulum: QR görüntüsü (Base64), gizli anahtar, otpauth URI (tek cihaz / manuel giriş). */
    @PostMapping(value = "/setup", produces = MediaType.APPLICATION_JSON_VALUE)
    public TwoFactorSetupResponse setupTotp(@RequestHeader("X-User-Id") String userIdHeader) {
        Integer userId = Integer.parseInt(userIdHeader.trim());
        return twoFactorService.prepareSecretAndSetupPayload(userId);
    }

    /**
     * Giriş: şifre veya Google sonrası dönen {@code twoFactorToken} ile Bearer; gövde 6 haneli TOTP.
     * Başarılı olunca tam access + refresh token döner.
     */
    @PostMapping("/login/verify")
    public ResponseEntity<TokenResponse> verifyLoginAfterPassword(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody TotpCodeRequest body) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization");
        }
        String token = authorization.substring(7).trim();
        jwtUtil.assertPendingTwoFactorToken(token);
        Integer userId = jwtUtil.extractUserId(token);
        twoFactorService.verifyTotpForPendingLogin(userId, body.getCode());
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(tokenService.generateToken(user));
    }

    @PostMapping("/active")
    public ResponseEntity<Void> active(
            @RequestHeader("X-User-Id") String userIdHeader,
            @Valid @RequestBody TotpCodeRequest body) {
        Integer userId = Integer.parseInt(userIdHeader.trim());
        twoFactorService.activateWithTotp(userId, body.getCode());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/disable")
    public ResponseEntity<Void> disable(
            @RequestHeader("X-User-Id") String userIdHeader,
            @Valid @RequestBody TotpCodeRequest body) {
        Integer userId = Integer.parseInt(userIdHeader.trim());
        twoFactorService.disableWithTotp(userId, body.getCode());
        return ResponseEntity.noContent().build();
    }
}
