package com.ael.authservice.dto.guest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Misafir kiralama: tek access token (+ izlenebilirlik için oturum kimliği). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestAccessTokenResponse {

    private String accessToken;

    /** JWT içindeki misafir oturumu (audit / rent ile hizalama). */
    private String guestSessionId;
}
