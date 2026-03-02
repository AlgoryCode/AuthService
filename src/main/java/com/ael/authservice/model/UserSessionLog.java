package com.ael.authservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name="user_sessions_logs")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionLog extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionLogId;

    @Column(nullable = false, unique = true, columnDefinition = "LONGTEXT")
    private String sessionId;


    @Column
    private LocalDateTime expiryDate;

    @Column
    private String userAgent;

    @Column(nullable = false, unique = true, columnDefinition = "LONGTEXT")
    private String refreshTokenId;

    @Column(nullable = false, unique = true, columnDefinition = "LONGTEXT")
    private String accessTokenId;

    private String proccessStatus;

    @Column
    private LocalDateTime revokedAt;

    private LocalDateTime refreshedAt;

    private LocalDateTime expiredAt;




    // Bu satırı geri ekleyin









}
