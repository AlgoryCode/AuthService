package com.ael.authservice.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Kullanıcıya rol atama / değiştirme (rol kodu). */
public record AssignUserRoleRequest(
        @NotBlank @Size(max = 64) String roleCode) {}
