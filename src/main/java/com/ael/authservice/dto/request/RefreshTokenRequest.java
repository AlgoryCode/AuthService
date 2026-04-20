package com.ael.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Refresh / logout / revoke isteklerinde çerez yerine JSON gövde ile taşınır. */
public record RefreshTokenRequest(@NotBlank String refreshToken) {}
