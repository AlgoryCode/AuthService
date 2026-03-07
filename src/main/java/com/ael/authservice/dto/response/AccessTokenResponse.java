package com.ael.authservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AccessTokenResponse {
    private String jti;
    private String session_id;
    private String accessToken;
}
