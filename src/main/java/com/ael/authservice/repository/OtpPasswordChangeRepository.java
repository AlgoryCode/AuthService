package com.ael.authservice.repository;

import com.ael.authservice.model.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpPasswordChangeRepository extends JpaRepository<UserOtp,Long> {
    UserOtp findOtpPasswordChangeByCode(String code);
}
