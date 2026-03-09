package com.ael.authservice.service;

import com.ael.authservice.mapper.UserMapper;
import com.ael.authservice.model.User;
import com.ael.authservice.repository.UserRepository;
import com.ael.authservice.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final GoogleService googleService;



    public UserResponse createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.toResponse(userRepository.save(user));
    }


    public Optional<UserResponse> findUserByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toResponse);
    }

    public UserResponse getUserById(Integer userId){
        return userMapper.toResponse(userRepository.findById(userId).get());
    }
}
