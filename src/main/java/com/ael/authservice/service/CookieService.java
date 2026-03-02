package com.ael.authservice.service;


import com.ael.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieService {

    @Value("${app.cookie.domain}")
    private String cookieDomain;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;


    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)           // JS erişemez - güvenlik!
                .secure(false)            // Development için false, PROD'da true
                .sameSite("Lax")          // CSRF koruması
                .path("/")                // Tüm path'lerde gönderilsin
                .maxAge(900)              // 15 dakika
                .domain(cookieDomain)
                .build();
    }

    public ResponseCookie createCsrfTokenCookie(String csrfToken) {
        return ResponseCookie.from("csrf_token", csrfToken)
                .httpOnly(false)          // JS erişebilmeli - header'a ekleyecek!
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(900)              // Access token ile aynı süre
                .domain(cookieDomain)
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)           // JS erişemez
                .secure(false)            // Dev için false, PROD'da true
                .sameSite("Lax")          // CSRF koruması
                .path("/auth")            // ⚠️ SADECE /auth/* endpoint'lerine gider!
                .maxAge(7 * 24 * 60 * 60) // 7 gün
                .domain(cookieDomain)
                .build();
    }


   /* public ResponseCookie createUserInfoCookie(User customer) {
        try {
            Map<String, Object> userInfo = Map.of(
                    "id", customer.getCustomerId(),
                    "email", customer.getEmail(),
                    "firstName", customer.getFirstName(),
                    "lastName", customer.getLastName(),
                    "role", customer.getRoles() != null ? customer.getRoles() : "USER"
            );

            String userInfoJson = objectMapper.writeValueAsString(userInfo);
            String encodedUserInfo = URLEncoder.encode(userInfoJson, StandardCharsets.UTF_8);

            return ResponseCookie.from("user_info", encodedUserInfo)
                    .httpOnly(false)      // JS erişebilmeli
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(900)
                    .domain("localhost")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user info cookie", e);
        }
    }
    */


    public ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .path("/auth")
                .maxAge(0)
                .domain(cookieDomain)
                .build();
    }

    public ResponseCookie clearAccessTokenCookie() {
        return ResponseCookie.from("access_token", "")
                .path("/")
                .maxAge(0)
                .domain(cookieDomain)
                .build();
    }

    public ResponseCookie clearCsrfTokenCookie() {
        return ResponseCookie.from("csrf_token", "")
                .path("/")
                .maxAge(0)
                .domain(cookieDomain)
                .build();
    }

    public ResponseCookie clearUserInfoCookie() {
        return ResponseCookie.from("user_info", "")
                .path("/")
                .maxAge(0)
                .domain(cookieDomain)
                .build();
    }

    public HttpHeaders createLoginHeaders(String accessToken, String refreshToken, String csrfToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createAccessTokenCookie(accessToken).toString());
        headers.add(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(refreshToken).toString());
        headers.add(HttpHeaders.SET_COOKIE, createCsrfTokenCookie(csrfToken).toString());
        return headers;
    }

    public HttpHeaders createLogoutHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, clearAccessTokenCookie().toString());
        headers.add(HttpHeaders.SET_COOKIE, clearRefreshTokenCookie().toString());
        headers.add(HttpHeaders.SET_COOKIE, clearCsrfTokenCookie().toString());
        return headers;
    }

    public HttpHeaders createRefreshHeaders(String accessToken, String refreshToken, String csrfToken) {
        return createLoginHeaders(accessToken, refreshToken, csrfToken);  // Aynı işlem
    }

}
