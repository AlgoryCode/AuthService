package com.ael.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@AllArgsConstructor
public class TwoFactorChallengeResponse {
    private String challengeId;
    private String userId;
    private String email;
    private String totpCode;
}
