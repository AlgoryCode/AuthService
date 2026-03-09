package com.ael.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class RefreshTokenResponse {
    private String refreshTokenId;
    private String refreshToken;
    private LocalDateTime expiryDate;
}
