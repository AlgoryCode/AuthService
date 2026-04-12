package com.ael.authservice.provider;


import com.ael.authservice.dto.request.AuthRequest;
import com.ael.authservice.dto.request.RegisterRequest;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.model.AuthType;

public interface AuthProvider<T extends AuthRequest> {
    AuthType supports();

    Class<T> requestType();

    UserResponse authenticate(T request);

    /** Bu sağlayıcı için açık kayıt (register) destekleniyor mu? */
    default boolean supportsRegistration() {
        return false;
    }

    /** {@link #register(RegisterRequest)} için beklenen istek sınıfı. */
    default Class<? extends RegisterRequest> registrationRequestType() {
        return null;
    }

    default UserResponse register(RegisterRequest request) {
        throw new UnsupportedOperationException("Registration not supported for " + supports());
    }
}
