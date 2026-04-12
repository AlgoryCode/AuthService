package com.ael.authservice.service;

import com.ael.authservice.dto.admin.AuthorityView;
import com.ael.authservice.dto.admin.RoleDetailView;
import com.ael.authservice.model.Authority;
import com.ael.authservice.model.Role;
import com.ael.authservice.model.User;
import com.ael.authservice.repository.AuthorityRepository;
import com.ael.authservice.repository.RoleRepository;
import com.ael.authservice.repository.UserRepository;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RbacAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;

    @Transactional(readOnly = true)
    public List<RoleDetailView> listRoles(String serviceFilter) {
        if (serviceFilter != null && !serviceFilter.isBlank()) {
            String sc = normalizeServiceCode(serviceFilter);
            return roleRepository.findByServiceCodeWithAuthoritiesOrdered(sc).stream()
                    .map(RbacAdminService::toRoleDetailView)
                    .toList();
        }
        return roleRepository.findAllRolesWithAuthoritiesOrdered().stream()
                .map(RbacAdminService::toRoleDetailView)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleDetailView getRole(String roleCode) {
        Role role = roleRepository
                .findByCodeWithAuthorities(normalizeCode(roleCode))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol bulunamadı"));
        return toRoleDetailView(role);
    }

    @Transactional(readOnly = true)
    public List<AuthorityView> listAuthorities() {
        return authorityRepository.findAllByOrderByCodeAsc().stream()
                .map(a -> new AuthorityView(a.getId(), a.getCode(), a.getDescription()))
                .toList();
    }

    @Transactional
    public void assignUserRole(int userId, String roleCode) {
        Role role = roleRepository
                .findByCode(normalizeCode(roleCode))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol bulunamadı"));
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
        user.setRole(role);
        user.setRoles(role.getCode());
        userRepository.save(user);
    }

    @Transactional
    public RoleDetailView grantAuthorityToRole(String roleCode, String authorityCode) {
        Role role = roleRepository
                .findByCodeWithAuthorities(normalizeCode(roleCode))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol bulunamadı"));
        Authority authority = authorityRepository
                .findByCode(normalizeCode(authorityCode))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Yetki bulunamadı"));

        if (role.getAuthorities() == null) {
            role.setAuthorities(new HashSet<>());
        }
        boolean already =
                role.getAuthorities().stream().anyMatch(a -> Objects.equals(a.getCode(), authority.getCode()));
        if (!already) {
            role.getAuthorities().add(authority);
            roleRepository.save(role);
        }
        Role refreshed =
                roleRepository.findByCodeWithAuthorities(role.getCode()).orElseThrow();
        return toRoleDetailView(refreshed);
    }

    @Transactional
    public RoleDetailView revokeAuthorityFromRole(String roleCode, String authorityCode) {
        Role role = roleRepository
                .findByCodeWithAuthorities(normalizeCode(roleCode))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol bulunamadı"));
        if (role.getAuthorities() == null || role.getAuthorities().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Yetki bu rolde tanımlı değil");
        }
        String ac = normalizeCode(authorityCode);
        boolean removed =
                role.getAuthorities().removeIf(a -> Objects.equals(a.getCode(), ac));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Yetki bu rolde tanımlı değil");
        }
        roleRepository.save(role);
        Role refreshed =
                roleRepository.findByCodeWithAuthorities(role.getCode()).orElseThrow();
        return toRoleDetailView(refreshed);
    }

    private static String normalizeCode(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kod boş olamaz");
        }
        return raw.trim();
    }

    private static String normalizeServiceCode(String raw) {
        return raw.trim().toUpperCase(Locale.ROOT);
    }

    private static RoleDetailView toRoleDetailView(Role role) {
        if (role.getAuthorities() == null || role.getAuthorities().isEmpty()) {
            return new RoleDetailView(role.getCode(), role.getName(), role.getServiceCode(), List.<String>of());
        }
        List<String> codes = role.getAuthorities().stream()
                .map(Authority::getCode)
                .filter(Objects::nonNull)
                .sorted(Comparator.naturalOrder())
                .distinct()
                .toList();
        return new RoleDetailView(role.getCode(), role.getName(), role.getServiceCode(), codes);
    }
}
