package com.ael.authservice.service;

import com.ael.authservice.dto.request.AuthRequest;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.model.AuthType;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFlowService {
    private final AuthService authService;
    private final TwoFactorChallengeService twoFactorChallengeService;
//
//    public <T extends AuthRequest> UserResponse buildAuthFlow(AuthType type, T request){
//        UserResponse  userResponse = authService.authenticate(type,request);
//
//
//    }

}
