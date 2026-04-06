package com.ael.authservice.service;

import com.ael.authservice.config.rabbitmq.MailQueueMessage;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.model.UserOtp;
import com.ael.authservice.repository.OtpPasswordChangeRepository;
import com.ael.authservice.repository.UserOtpRepository;
import com.ael.authservice.util.GenerateOTP;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserOtpService {

    private final UserOtpRepository userOtpRepository;
    private final UserService userService;
    private final OtpPasswordChangeRepository otpPasswordChangeRepository;
    private final GenerateOTP generateOTP;
    private final MailQueuePublisher mailQueuePublisher;


    public void deactivateOldOtp(String email) {

        UserResponse foundedUser = userService
                .findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        userOtpRepository.deactivateOldOtp(Long.valueOf(foundedUser.getUserId()));
    }

    public String verifyOtp(String code){
       return "sadsad";
    }

    public boolean isValidOtp(String code) {
        return otpPasswordChangeRepository.findOtpPasswordChangeByCode(code).getUsage();
    }

    public String generateOtp(String email) {

        deactivateOldOtp(email);

        mailQueuePublisher.publish(MailQueueMessage.builder()
                .to("trkhamarat@gmail.com")
                .body(generateOTP.generateOTP())
                .subject("OTP PASSWORD SENDED!").build());

        return "Yes!";
    }
}
