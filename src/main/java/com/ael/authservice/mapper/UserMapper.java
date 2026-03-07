package com.ael.authservice.mapper;


import com.ael.authservice.model.User;
import com.ael.authservice.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;



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
}
