package com.ael.authservice.dto.request;

import lombok.Data;

/** PATCH: null alanlar değiştirilmez. */
@Data
public class MyProfilePatchRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private Boolean notifyEmailImportant;
    private Boolean notifyScanAlerts;
    private Boolean notifyWeeklyReport;
    private Boolean notifyMarketingEmails;
    private Boolean notifyPushBrowser;
}
