package com.ael.authservice.service;


import com.ael.authservice.repository.OtpPasswordChangeRepository;
import com.ael.authservice.util.GenerateOTP;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpPasswordChangeService {
  private final OtpPasswordChangeRepository otpPasswordChangeRepository;
  private final GenerateOTP generateOTP;

  public String createOtpToPasswordChange(){
         return generateOTP.generateOTP();
  }

}
