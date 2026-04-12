package com.ael.authservice.config;

import com.ael.authservice.model.Authority;
import com.ael.authservice.model.Role;
import com.ael.authservice.model.User;
import com.ael.authservice.repository.AuthorityRepository;
import com.ael.authservice.repository.RoleRepository;
import com.ael.authservice.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Platform geneli {@code USER} / {@code ADMIN} rolleri ve kullanıcı onarımı.
 *
 * <p>RENT / QR servis rolleri ve ayrıntılı yetkiler veritabanında kalır; tek seferlik yüklemek için
 * {@code src/main/resources/db/seed-rbac-rent-qr.sql} dosyasını kullanın (yeniden başlatmada
 * üzerine yazılmaz).
 */
@Component
@Order(0)
@RequiredArgsConstructor
public class RoleAuthorityDataInitializer implements ApplicationRunner {

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Authority accountRead = upsertAuthority("ACCOUNT_READ", "Kendi profil bilgisini okuma");
        Authority accountWrite = upsertAuthority("ACCOUNT_WRITE", "Kendi profil bilgisini güncelleme");
        Authority rentRead = upsertAuthority("RENT_READ", "Kiralama verilerini okuma");
        Authority rentWrite = upsertAuthority("RENT_WRITE", "Kiralama talebi oluşturma / güncelleme");
        Authority rbacManage = upsertAuthority("RBAC_MANAGE", "Rol ve yetki yönetimi (admin API)");

        Role userRole =
                upsertRole(
                        "USER",
                        "Standart kullanıcı",
                        null,
                        Set.of(accountRead, accountWrite, rentRead, rentWrite));

        upsertRole(
                "ADMIN",
                "Yönetici",
                null,
                Set.of(accountRead, accountWrite, rentRead, rentWrite, rbacManage));

        List<User> toFix =
                userRepository.findAll().stream().filter(u -> u.getRole() == null).peek(u -> u.setRole(userRole)).toList();
        if (!toFix.isEmpty()) {
            userRepository.saveAll(toFix);
        }
    }

    private Authority upsertAuthority(String code, String description) {
        return authorityRepository
                .findByCode(code)
                .orElseGet(
                        () ->
                                authorityRepository.save(
                                        Authority.builder().code(code).description(description).build()));
    }

    private Role upsertRole(String code, String name, String serviceCode, Set<Authority> authorities) {
        return roleRepository
                .findByCode(code)
                .map(
                        existing -> {
                            existing.setName(name);
                            existing.setServiceCode(serviceCode);
                            if (existing.getAuthorities() == null) {
                                existing.setAuthorities(new HashSet<>());
                            }
                            existing.getAuthorities().clear();
                            existing.getAuthorities().addAll(authorities);
                            return roleRepository.save(existing);
                        })
                .orElseGet(
                        () ->
                                roleRepository.save(
                                        Role.builder()
                                                .code(code)
                                                .name(name)
                                                .serviceCode(serviceCode)
                                                .authorities(new HashSet<>(authorities))
                                                .build()));
    }
}
