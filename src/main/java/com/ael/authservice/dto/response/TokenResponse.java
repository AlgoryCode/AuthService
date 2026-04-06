package com.ael.authservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    /** İstemcinin localStorage kullanıcı özetini doldurması için (JWT içinde e-posta yok). */
    private Integer userId;
    private String email;
    private String firstName;
    private String lastName;
}
