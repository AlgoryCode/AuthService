package com.ael.authservice.dto.admin;

import java.util.List;

/** Rol ve bağlı yetki kodları (sıralı). {@code serviceCode} yalnızca servis-özel roller için dolu. */
public record RoleDetailView(String code, String name, String serviceCode, List<String> authorityCodes) {}
