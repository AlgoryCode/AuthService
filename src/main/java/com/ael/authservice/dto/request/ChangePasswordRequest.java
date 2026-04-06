package com.ael.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Mevcut şifre gerekli")
    private String currentPassword;

    @NotBlank(message = "Yeni şifre gerekli")
    @Size(min = 8, message = "Yeni şifre en az 8 karakter olmalı")
    private String newPassword;
}
