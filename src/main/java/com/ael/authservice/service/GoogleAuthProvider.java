package com.ael.authservice.service;

import com.ael.authservice.dto.request.GoogleAuthRequest;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.mapper.UserMapper;
import com.ael.authservice.model.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleAuthProvider implements AuthProvider<GoogleAuthRequest> {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public AuthType supports() {
        return AuthType.GOOGLE;
    }

    @Override
    public Class<GoogleAuthRequest> requestType() {
        return GoogleAuthRequest.class;
    }

    @Override
    public UserResponse authenticate(GoogleAuthRequest request) {
        Payload payload = verify(request.googleAccountToken());

        return userService.findUserByEmail(payload.getEmail())
                .orElseGet(() -> {
                    User newUser = userMapper.PayloadToUser(payload);
                    return userService.createUser(newUser);
                });
    }

    public Payload verify(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(googleClientId)).build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new RuntimeException("Invalid Google ID token");
            }

            Payload payload = idToken.getPayload();
            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                throw new RuntimeException("Email not verified");
            }

            return payload;
        } catch (Exception e) {
            throw new RuntimeException("Google token verify failed", e);
        }
    }
}