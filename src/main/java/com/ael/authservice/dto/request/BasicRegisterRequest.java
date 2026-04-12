package com.ael.authservice.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * E-posta/şifre kaydı. {@code registrationRole} veya geriye dönük {@code roleCode} ile rol seçilebilir.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BasicRegisterRequest(
        @JsonProperty("registrationRole") @JsonAlias("roleCode") RegistrationRole registrationRole,
        @NotBlank @Email String email,
        @NotBlank String password,
        String firstName,
        String lastName,
        String phoneNumber,
        String address,
        String city,
        String username)
        implements RegisterRequest {

    public BasicRegisterRequest {
        if (registrationRole == null) {
            registrationRole = RegistrationRole.USER;
        }
    }
}
