package com.ael.authservice.repository;

import com.ael.authservice.model.OtpPasswordChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpPasswordChangeRepository extends JpaRepository<OtpPasswordChange,Long> {
}
