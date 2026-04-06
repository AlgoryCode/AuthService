package com.ael.authservice.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyProfileResponse {

    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private boolean twoFactorEnabled;
    private LocalDateTime memberSince;

    private boolean notifyEmailImportant;
    private boolean notifyScanAlerts;
    private boolean notifyWeeklyReport;
    private boolean notifyMarketingEmails;
    private boolean notifyPushBrowser;
}
