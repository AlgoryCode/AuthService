package com.ael.authservice.repository;


import com.ael.authservice.model.UserSessionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserSessionLog extends JpaRepository<UserSessionLog, Long> {
    UserSessionLog findBySessionId(String sessionId);
}
