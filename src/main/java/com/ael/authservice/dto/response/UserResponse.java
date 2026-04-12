package com.ael.authservice.dto.response;

import java.util.List;
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

    /** JWT {@code role} claim ile uyumlu rol kodu (örn. USER). */
    private String roleCode;

    /** JWT {@code authorities} claim ile uyumlu yetki kodları. */
    private List<String> authorities;
}
