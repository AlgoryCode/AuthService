package com.ael.authservice.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class GenerateOTP {
    public String generateOTP() {
        return String.format("%06d", new SecureRandom().nextInt(999_999));
    }
}
