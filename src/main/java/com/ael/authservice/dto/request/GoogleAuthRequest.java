package com.ael.authservice.dto.request;

public record GoogleAuthRequest(String googleAccountToken) implements AuthRequest {
}
