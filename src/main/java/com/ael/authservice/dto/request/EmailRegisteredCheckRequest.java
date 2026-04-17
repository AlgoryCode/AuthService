package com.ael.authservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Kiralama (misafir) akışında: adresin zaten kayıtlı kullanıcıya ait olup olmadığını kontrol için.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EmailRegisteredCheckRequest(@NotBlank @Email String email) {}
