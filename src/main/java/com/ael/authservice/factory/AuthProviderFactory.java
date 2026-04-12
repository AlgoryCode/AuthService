package com.ael.authservice.factory;

import com.ael.authservice.dto.request.AuthRequest;
import com.ael.authservice.dto.request.RegisterRequest;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.model.AuthType;
import com.ael.authservice.provider.AuthProvider;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthProviderFactory {

    private final Map<AuthType, AuthProvider<?>> providers = new EnumMap<>(AuthType.class);

    public AuthProviderFactory(List<AuthProvider<?>> authProviders) {
        for (AuthProvider<?> provider : authProviders) {
            providers.put(provider.supports(), provider);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AuthRequest> AuthProvider<T> get(AuthType type, Class<T> requestType) {
        AuthProvider<?> provider = providers.get(type);

        if (provider == null) {
            throw new IllegalArgumentException("No provider registered for type: " + type);
        }

        AuthProvider<T> typedProvider = (AuthProvider<T>) provider;

        if (!typedProvider.requestType().equals(requestType)) {
            throw new IllegalArgumentException(
                    "Request type mismatch for " + type +
                            ". Expected: " + typedProvider.requestType().getSimpleName() +
                            ", Actual: " + requestType.getSimpleName()
            );
        }

        return typedProvider;
    }

    public UserResponse register(AuthType type, RegisterRequest request) {
        AuthProvider<?> provider = providers.get(type);
        if (provider == null || !provider.supportsRegistration()) {
            throw new IllegalArgumentException("Registration not supported for: " + type);
        }
        Class<?> expected = provider.registrationRequestType();
        if (expected == null || !expected.isInstance(request)) {
            throw new IllegalArgumentException(
                    "Registration request type mismatch for "
                            + type
                            + ". Expected: "
                            + (expected == null ? "?" : expected.getSimpleName())
                            + ", actual: "
                            + request.getClass().getSimpleName());
        }
        return provider.register(request);
    }
}