package com.ael.authservice.service;

import com.ael.authservice.dto.request.BasicAuthRequest;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.mapper.UserMapper;
import com.ael.authservice.model.User;
import com.ael.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthProvider implements AuthProvider<BasicAuthRequest> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthType supports() {
        return AuthType.BASIC;
    }

    @Override
    public Class<BasicAuthRequest> requestType() {
        return BasicAuthRequest.class;
    }

    @Override
    public UserResponse authenticate(BasicAuthRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return userMapper.toResponse(user);
    }

}