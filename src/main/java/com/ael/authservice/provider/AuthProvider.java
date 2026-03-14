package com.ael.authservice.provider;


import com.ael.authservice.dto.request.AuthRequest;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.model.AuthType;

public interface AuthProvider<T extends AuthRequest> {
        AuthType supports();
        Class<T> requestType();
        UserResponse authenticate(T request);
        //String logout();
}
