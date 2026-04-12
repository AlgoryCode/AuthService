package com.ael.authservice.service;

import com.ael.authservice.dto.request.AuthRequest;
import com.ael.authservice.dto.request.RegisterRequest;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.factory.AuthProviderFactory;
import com.ael.authservice.model.AuthType;
import com.ael.authservice.provider.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthProviderFactory authProviderFactory;

    @SuppressWarnings("unchecked")
    public <T extends AuthRequest> UserResponse authenticate(AuthType type, T request) {

        AuthProvider<T> provider =
                authProviderFactory.get(type, (Class<T>) request.getClass());
        return provider.authenticate(request);
    }

    public UserResponse register(AuthType type, RegisterRequest request) {
        return authProviderFactory.register(type, request);
    }
}