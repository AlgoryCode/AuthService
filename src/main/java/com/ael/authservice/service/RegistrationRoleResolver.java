package com.ael.authservice.service;

import com.ael.authservice.config.properties.RbacAppProperties;
import com.ael.authservice.dto.request.RegistrationRole;
import com.ael.authservice.model.Role;
import com.ael.authservice.repository.RoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class RegistrationRoleResolver {

    private final RoleRepository roleRepository;
    private final RbacAppProperties rbacAppProperties;

    public Role resolve(RegistrationRole registrationRole) {
        if (registrationRole == RegistrationRole.USER) {
            return roleRepository
                    .findByCode("USER")
                    .orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.INTERNAL_SERVER_ERROR, "USER rolü tanımlı değil"));
        }
        String code = registrationRole.roleCode();
        List<String> allowed = rbacAppProperties.getRegistrationAllowedRoleCodes();
        if (allowed == null || allowed.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Kayıt için rol seçimi yapılandırılmamış; registrationRole gönderilemez");
        }
        boolean ok = allowed.stream().anyMatch(a -> a != null && a.equalsIgnoreCase(code));
        if (!ok) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kayıt için bu rol kullanılamaz: " + code);
        }
        String normalized =
                allowed.stream()
                        .filter(a -> a != null && a.equalsIgnoreCase(code))
                        .findFirst()
                        .orElse(code);
        return roleRepository
                .findByCode(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol bulunamadı: " + normalized));
    }
}
