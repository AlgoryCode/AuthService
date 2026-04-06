package com.ael.authservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Integer userId;
    private String firstName;
    private String familyName;
    private String email;
    private String phoneNumber;
    private boolean isTwoFactorEnabled;

}
