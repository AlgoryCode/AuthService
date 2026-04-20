package com.ael.authservice.controller;

import com.ael.authservice.dto.guest.GuestAccessTokenRequest;
import com.ael.authservice.dto.guest.GuestAccessTokenResponse;
import com.ael.authservice.service.guest.GuestRentalAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Misafir kiralama kimliği — {@code BasicAuthController} dışında tutulur.
 * <p>Spring Security: üretimde {@code /guest/**} için {@code permitAll} + rate limit önerilir.</p>
 */
@RestController
@RequestMapping("/guest")
@RequiredArgsConstructor
public class GuestAuthController {

    private final GuestRentalAccessService guestRentalAccessService;

    /** Yalnızca access JWT (refresh yok). */
    @PostMapping("/access-token")
    public ResponseEntity<GuestAccessTokenResponse> issueAccessToken(
            @Valid @RequestBody GuestAccessTokenRequest request) {
        return ResponseEntity.ok(guestRentalAccessService.issueAccessToken(request.email()));
    }
}
