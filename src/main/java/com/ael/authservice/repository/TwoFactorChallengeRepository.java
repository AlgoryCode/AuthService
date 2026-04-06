package com.ael.authservice.repository;

import com.ael.authservice.model.TwoFactorChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwoFactorChallengeRepository extends JpaRepository<TwoFactorChallenge,String> {
}
