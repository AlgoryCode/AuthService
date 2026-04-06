package com.ael.authservice.dto.response;

/**
 * TOTP kurulumu: tek cihazda manuel anahtar ve mobil uygulama derin bağlantısı için.
 */
public record TwoFactorSetupResponse(
        String secret,
        String issuer,
        String accountLabel,
        String qrImageBase64,
        String otpAuthUri
) {}
