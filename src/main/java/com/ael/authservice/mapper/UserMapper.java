package com.ael.authservice.mapper;

import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.model.Authority;
import com.ael.authservice.model.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class UserMapper {
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .familyName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .isTwoFactorEnabled(user.isTwoFactorEnabled())
                .roleCode(resolveRoleCode(user))
                .authorities(resolveAuthorityCodes(user))
                .build();
    }

    private static String resolveRoleCode(User user) {
        if (user.getRole() != null) {
            return user.getRole().getCode();
        }
        if (user.getRoles() != null && !user.getRoles().isBlank()) {
            return user.getRoles().split(",")[0].trim();
        }
        return "USER";
    }

    private static List<String> resolveAuthorityCodes(User user) {
        if (user.getRole() == null || user.getRole().getAuthorities() == null) {
            return List.of();
        }
        return user.getRole().getAuthorities().stream()
                .map(Authority::getCode)
                .sorted()
                .distinct()
                .toList();
    }

    public User PayloadToUser(Payload payload){
        return User.builder()
                .email(payload.getEmail())
                .firstName((String) payload.get("given_name"))
                .lastName((String) payload.get("family_name"))
                .provider("google")
                .providerId(payload.getSubject())
                .username(payload.getEmail())
                .phoneNumber(null)
                .address(null)
                .city(null)
                .password("GOOGLE_AUTH")
                .roles("USER")
                .build();
    }


}
