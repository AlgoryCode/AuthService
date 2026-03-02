package com.ael.authservice.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AccessTokenRequest {
    private String accessToken;
}
