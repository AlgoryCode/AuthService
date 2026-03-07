package com.ael.authservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class GuestUserInfo {
    private String guestUID;
    private LocalDateTime createdDate;
    private String guestRole;
}
