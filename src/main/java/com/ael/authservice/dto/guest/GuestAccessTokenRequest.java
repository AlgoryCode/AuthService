package com.ael.authservice.dto.guest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Kiralama (misafir) için yalnızca access JWT; refresh üretilmez. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GuestAccessTokenRequest(@NotBlank @Email String email) {}
