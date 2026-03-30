package com.ael.authservice.controller;

import com.ael.authservice.model.OtpPasswordChange;
import com.ael.authservice.service.OtpPasswordChangeService;
import com.ael.authservice.util.GenerateOTP;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final OtpPasswordChangeService otpPasswordChangeService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(){
        otpPasswordChangeService.createOtpToPasswordChange();
        return ResponseEntity.ok("Password change request created successfully");
    }

}
