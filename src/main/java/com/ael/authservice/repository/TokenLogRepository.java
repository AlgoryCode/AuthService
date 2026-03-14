package com.ael.authservice.repository;


import com.ael.authservice.model.TokenLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenLogRepository extends JpaRepository<TokenLog, Long> {
    TokenLog findBySessionId(String sessionId);

    TokenLog findTokenLogByUserId(Integer userId);
}
