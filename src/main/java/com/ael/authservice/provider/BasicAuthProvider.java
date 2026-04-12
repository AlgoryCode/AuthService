package com.ael.authservice.provider;

import com.ael.authservice.dto.request.BasicAuthRequest;
import com.ael.authservice.dto.request.BasicRegisterRequest;
import com.ael.authservice.dto.request.RegisterRequest;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.mapper.UserMapper;
import com.ael.authservice.model.AuthType;
import com.ael.authservice.model.User;
import com.ael.authservice.repository.UserRepository;
import com.ael.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthProvider implements AuthProvider<BasicAuthRequest> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

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
        User user = userRepository
                .findByEmailWithRoleAndAuthorities(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }






        return userMapper.toResponse(user);
    }

    @Override
    public boolean supportsRegistration() {
        return true;
    }

    @Override
    public Class<? extends RegisterRequest> registrationRequestType() {
        return BasicRegisterRequest.class;
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        BasicRegisterRequest r = (BasicRegisterRequest) request;
        if (userService.existsByEmail(r.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu e-posta zaten kayıtlı");
        }
        User user =
                User.builder()
                        .email(r.email())
                        .password(r.password())
                        .firstName(r.firstName())
                        .lastName(r.lastName())
                        .phoneNumber(r.phoneNumber())
                        .address(r.address())
                        .city(r.city())
                        .username(r.username())
                        .build();
        return userService.registerNewUser(user, r.registrationRole());
    }
}