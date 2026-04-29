package com.ael.authservice.repository;

import com.ael.authservice.model.UserOtp;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserOtpRepository extends JpaRepository<UserOtp,Long> {

    @Modifying
    @Transactional
    @Query("UPDATE UserOtp usr SET usr.isUsed = true " +
            "WHERE usr.userId = :userId AND usr.isUsed = false")
    void deactivateOldOtp(@Param("userId") Long userId);

    List<UserOtp> findUserOtpByCode(String code);
}
