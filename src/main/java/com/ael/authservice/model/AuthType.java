package com.ael.authservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthType {
    BASIC("basic"),
    GOOGLE("google");

    private final String value;

    public static AuthType from(String raw){
        for (AuthType t : values()) {
            if (t.value.equalsIgnoreCase(raw)) return t;
        }
        throw new IllegalArgumentException("Unsupported auth type: " + raw);
    }
}
