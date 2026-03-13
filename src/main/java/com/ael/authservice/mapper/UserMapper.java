package com.ael.authservice.mapper;

import com.ael.authservice.model.User;
import com.ael.authservice.dto.response.UserResponse;
import org.springframework.stereotype.Component;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;


@Component
public final class UserMapper {
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .familyName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public User PayloadToUser(Payload payload){
        return User.builder()
                .email(payload.getEmail())
                .firstName((String) payload.get("given_name"))
                .lastName((String) payload.get("given_name"))
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
