package com.ael.authservice.service.guest;

import com.ael.authservice.dto.guest.GuestAccessTokenResponse;
import com.ael.authservice.util.JwtUtil;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Üye oturumundan ayrı: refresh / {@code TokenLog} yok; yalnızca kısa ömürlü access JWT.
 * Rent gateway bu token ile sınırlı uçlara izin verebilir ({@code RENT_GUEST}).
 */
@Service
@RequiredArgsConstructor
public class GuestRentalAccessService {

    private final JwtUtil jwtUtil;

    public GuestAccessTokenResponse issueAccessToken(String rawEmail) {
        if (!StringUtils.hasText(rawEmail)) {
            throw new IllegalArgumentException("E-posta gerekli");
        }
        String email = rawEmail.trim().toLowerCase(Locale.ROOT);
        String guestSessionId = UUID.randomUUID().toString();
        String accessToken = jwtUtil.generateGuestAccessToken(email, guestSessionId);
        return GuestAccessTokenResponse.builder()
                .accessToken(accessToken)
                .guestSessionId(guestSessionId)
                .build();
    }
}
