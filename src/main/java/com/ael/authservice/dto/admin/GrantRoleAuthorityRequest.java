package com.ael.authservice.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Role yetki ekleme isteği. */
public record GrantRoleAuthorityRequest(
        @NotBlank @Size(max = 128) String authorityCode) {}
