package com.ael.authservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Table(name="token_logs")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TokenLog extends BaseModel {

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

    private String proccessStatus;

    @Column(name = "is_revoked")
    private boolean revoked = false;

    private LocalDateTime revokedAt;

    private LocalDateTime refreshedAt;
}
