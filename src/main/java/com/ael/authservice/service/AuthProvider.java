package com.ael.authservice.service;


import com.ael.authservice.dto.request.AuthRequest;
import com.ael.authservice.dto.response.UserResponse;

public interface AuthProvider<T extends AuthRequest> {
        AuthType supports();
        Class<T> requestType();
        UserResponse authenticate(T request);
}
