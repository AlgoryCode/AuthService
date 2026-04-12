package com.ael.authservice.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    @Column(nullable = true)
    private String username;
    private String provider;
    private String providerId;
    private String password;
    private boolean twoFactorEnabled;
    private String totpSecret;
    /** Eski metin alanı (örn. USER); yeni akışta {@link #role} önceliklidir. */
    private String roles;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "notify_email_important", nullable = false)
    @Builder.Default
    private boolean notifyEmailImportant = false;

    @Column(name = "notify_scan_alerts", nullable = false)
    @Builder.Default
    private boolean notifyScanAlerts = false;

    @Column(name = "notify_weekly_report", nullable = false)
    @Builder.Default
    private boolean notifyWeeklyReport = false;

    @Column(name = "notify_marketing_emails", nullable = false)
    @Builder.Default
    private boolean notifyMarketingEmails = false;

    @Column(name = "notify_push_browser", nullable = false)
    @Builder.Default
    private boolean notifyPushBrowser = false;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

}