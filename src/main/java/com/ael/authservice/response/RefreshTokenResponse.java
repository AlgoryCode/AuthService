package com.ael.authservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RefreshTokenResponse {
    private String refreshTokenId;
    private String refreshToken;
}
