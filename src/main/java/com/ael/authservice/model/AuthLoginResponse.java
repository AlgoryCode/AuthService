package com.ael.authservice.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
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