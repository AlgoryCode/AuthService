package com.ael.authservice.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Locale;

/**
 * Kayıt sırasında atanabilecek roller. JSON değeri enum adı ile aynı olmalıdır (örn. {@code RENT_USER}).
 * Veritabanında {@code roles.code} ile eşleşir; {@link #USER} → {@code USER} satırı.
 */
public enum RegistrationRole {
    USER,
    RENT_USER,
    RENT_MANAGER,
    RENT_ADMIN,
    QR_USER,
    QR_MANAGER,
    QR_ADMIN;

    @JsonCreator
    public static RegistrationRole fromJson(String raw) {
        if (raw == null || raw.isBlank()) {
            return USER;
        }
        try {
            return RegistrationRole.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Geçersiz kayıt rolü: " + raw);
        }
    }

    /** Veritabanı {@code roles.code} değeri (enum adı ile aynı). */
    public String roleCode() {
        return name();
    }
}
