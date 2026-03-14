package com.ael.authservice.dto.request;

import javax.annotation.Nullable;

public record BasicAuthRequest(String email, String password) implements AuthRequest {
}
