package com.ael.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthLoginResponse {
    @Builder.Default
    private String tokenType = "Bearer";
    private String accessToken;
    private Long expiresIn;
    private String userName;
    private Integer customerId;
    private Integer activeBasketId;
}