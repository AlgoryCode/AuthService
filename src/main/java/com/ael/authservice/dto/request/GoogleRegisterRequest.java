package com.ael.authservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

/** Google ID token ile açık kayıt (login sırasındaki otomatik oluşturmadan ayrı). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleRegisterRequest(@NotBlank String idToken, RegistrationRole registrationRole)
        implements RegisterRequest {

    public GoogleRegisterRequest {
        if (registrationRole == null) {
            registrationRole = RegistrationRole.USER;
        }
    }
}
