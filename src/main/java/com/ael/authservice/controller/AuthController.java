package com.ael.authservice.controller;

import com.ael.authservice.dto.request.GoogleLoginRequest;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.mapper.UserMapper;
import com.ael.authservice.model.User;
import com.ael.authservice.model.UserSessionLog;
import com.ael.authservice.dto.response.AccessTokenResponse;
import com.ael.authservice.dto.response.RefreshTokenResponse;
import com.ael.authservice.service.AuthService;
import com.ael.authservice.service.CookieService;
import com.ael.authservice.service.GoogleService;
import com.ael.authservice.service.UserService;
import com.ael.authservice.service.UserSessionLogService;
import com.ael.authservice.util.JwtUtil;
import com.ael.authservice.model.LoginRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.util.DateTime;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RefreshScope
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private AuthService authService;
    private final CookieService cookieService;
    private final GoogleService googleService;
    private final UserSessionLogService userSessionLogService;
    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {


        UserResponse user = userService.findUserByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        AccessTokenResponse accessTokenResponse = jwtUtil.generateAccessToken(user);
        RefreshTokenResponse refreshTokenResponse = jwtUtil.generateRefreshToken(user, accessTokenResponse.getJti(), accessTokenResponse.getSession_id());
        String csrfToken = jwtUtil.generateCsrfToken();

        userSessionLogService.createSessionLog(UserSessionLog.builder()
                .refreshTokenId(refreshTokenResponse.getRefreshTokenId())
                .accessTokenId(accessTokenResponse.getJti())
                .sessionId(accessTokenResponse.getSession_id())
                .userAgent("")
                .build());

        return ResponseEntity.ok()
                .header(cookieService.createLoginHeaders(
                        accessTokenResponse.getAccessToken(),
                        refreshTokenResponse.getRefreshToken(),
                        csrfToken).toString()).body("Succes");
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue String access_token) {
        try {

            String sessionId = jwtUtil.extractSessionIdFromToken(access_token);
            Integer userId = jwtUtil.extractUserIdFromToken(access_token);

            userSessionLogService.revokeUserSession(sessionId, userId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookieService.clearRefreshTokenCookie().toString())
                    .header(HttpHeaders.SET_COOKIE, cookieService.clearCsrfTokenCookie().toString())
                    .header(HttpHeaders.SET_COOKIE, cookieService.clearAccessTokenCookie().toString())
                    .body("Success");

        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(500).body("Logout failed");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.ok("User Created Successfully");
    }

    @PostMapping("/refreshAccessToken")
    public ResponseEntity<?> refreshAccessToken(@CookieValue String access_token) {

        String session_id = jwtUtil.extractSessionIdFromToken(access_token);
        Integer userId = jwtUtil.extractUserIdFromToken(access_token);
        if (userSessionLogService.getRefreshTokenBySessionId(session_id).isBefore(LocalDateTime.now())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token Expired");


        }
        UserResponse user = userService.getUserById(userId);
        AccessTokenResponse accessTokenResponse = jwtUtil.generateAccessToken(user);
        RefreshTokenResponse refreshTokenResponse = jwtUtil.generateRefreshToken(user, accessTokenResponse.getJti(), accessTokenResponse.getSession_id());
        String csrfToken = jwtUtil.generateCsrfToken();



        return ResponseEntity.ok()
                .header(cookieService.createLoginHeaders(
                        accessTokenResponse.getAccessToken(),
                        refreshTokenResponse.getRefreshToken(),
                        csrfToken).toString()).body(user);
    }

    @PostMapping("/google/login")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        UserResponse user = userMapper.toResponse(userMapper.PayloadToUser(googleService.verify(request.getIdToken())));
        AccessTokenResponse accessTokenResponse = jwtUtil.generateAccessToken(user);
        RefreshTokenResponse refreshTokenResponse = jwtUtil.generateRefreshToken(user, accessTokenResponse.getJti(), accessTokenResponse.getSession_id());
        String csrfToken = jwtUtil.generateCsrfToken();

        userSessionLogService.createSessionLog(UserSessionLog.builder()
                .refreshTokenId(refreshTokenResponse.getRefreshTokenId())
                .accessTokenId(accessTokenResponse.getJti())
                .sessionId(accessTokenResponse.getSession_id())
                .expiryDate(refreshTokenResponse.getExpiryDate())
                .userAgent("")
                .build());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService
                        .createAccessTokenCookie(accessTokenResponse.getAccessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieService
                        .createRefreshTokenCookie(refreshTokenResponse.getRefreshToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieService
                        .createCsrfTokenCookie(csrfToken).toString())
                .body(user);
//                .headers(cookieService.createLoginHeaders(
//                        accessTokenResponse.getAccessToken(),
//                        refreshTokenResponse.getRefreshToken(),
//                        csrfToken)).body("Succes");

    }

    @PostMapping("/google/register")
    public ResponseEntity<UserResponse> googleRegister(@RequestBody GoogleLoginRequest request) {
        return ResponseEntity.ok(userService.createUser(userMapper.PayloadToUser(googleService.verify(request.getIdToken()))));
    }

    @PostMapping("/revoke-refreshToken")
    public ResponseEntity<String> revokeRefreshToken(@CookieValue String access_token) {
        String uuid = jwtUtil.extractSessionIdFromToken(access_token);
        return ResponseEntity.ok("Refresh token revoked");
    }

    @GetMapping("/list-activeSession")
    public ResponseEntity<List<UserSessionLog>> listActiveSession(@RequestHeader("X-Customer-Id") Integer customerId) {
        return ResponseEntity.ok(userSessionLogService.getActiveUserSessions(customerId));
    }


}





