package com.ael.authservice.dto.request;

public record GoogleAuthRequest(String idToken) implements AuthRequest {
}
