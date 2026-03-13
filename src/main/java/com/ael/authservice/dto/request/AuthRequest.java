package com.ael.authservice.dto.request;

public sealed interface AuthRequest permits BasicAuthRequest,GoogleAuthRequest {
}
