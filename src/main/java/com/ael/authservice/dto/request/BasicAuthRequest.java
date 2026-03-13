package com.ael.authservice.dto.request;

public record BasicAuthRequest(String email,String password) implements AuthRequest {
}
