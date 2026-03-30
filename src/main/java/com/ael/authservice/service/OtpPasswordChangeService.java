package com.ael.authservice.service;


import com.ael.authservice.config.rabbitmq.MailQueueMessage;
import com.ael.authservice.repository.OtpPasswordChangeRepository;
import com.ael.authservice.util.GenerateOTP;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpPasswordChangeService {
    private final OtpPasswordChangeRepository otpPasswordChangeRepository;
    private final GenerateOTP generateOTP;
    private final MailQueuePublisher mailQueuePublisher;

    public String createOtpToPasswordChange() {
        mailQueuePublisher.publish(MailQueueMessage.builder()
                .to("trkhamarat@gmail.com")
                .body(generateOTP.generateOTP())
                .subject("OTP PASSWORD SENDED!").build());

        return "Yes!";
    }

}
