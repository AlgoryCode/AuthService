package com.ael.authservice.config;

import com.ael.authservice.model.Authority;
import com.ael.authservice.model.Role;
import com.ael.authservice.reference.RbacReferenceCatalog;
import com.ael.authservice.reference.RbacReferenceCatalog.QrAdminAuthority;
import com.ael.authservice.reference.RbacReferenceCatalog.QrManagerAuthority;
import com.ael.authservice.reference.RbacReferenceCatalog.QrRole;
import com.ael.authservice.reference.RbacReferenceCatalog.QrUserAuthority;
import com.ael.authservice.reference.RbacReferenceCatalog.RentAdminAuthority;
import com.ael.authservice.reference.RbacReferenceCatalog.RentManagerAuthority;
import com.ael.authservice.reference.RbacReferenceCatalog.RentRole;
import com.ael.authservice.reference.RbacReferenceCatalog.RentUserAuthority;
import com.ael.authservice.repository.AuthorityRepository;
import com.ael.authservice.repository.RoleRepository;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RENT / QR servis rolleri ve CRUD yetkilerini veritabanına yazar. İdempotent: eksik kayıt ekler, mevcut
 * rollerin yetki kümesini sıfırlamaz.
 *
 * <p>{@code spring.sql.init.mode} kapalı olduğundan {@code seed-rbac-rent-qr.sql} otomatik çalışmaz; bu
 * sınıf aynı veriyi uygulama başlangıcında üretir. İsterseniz {@code app.rbac.rent-qr-seed-enabled=false}
 * ile kapatıp yalnızca SQL ile yükleyebilirsiniz.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "app.rbac",
        name = "rent-qr-seed-enabled",
        havingValue = "true",
        matchIfMissing = true)
public class ServiceRbacBootstrap implements ApplicationRunner {

    private static final Map<String, String> AUTHORITY_DESCRIPTIONS = new HashMap<>();

    static {
        putRentQr(
                "RENT_ADMIN_READ",
                "RENT_ADMIN_UPDATE",
                "RENT_ADMIN_WRITE",
                "RENT_ADMIN_DELETE",
                "Kiralama admin — okuma",
                "Kiralama admin — güncelleme",
                "Kiralama admin — oluşturma",
                "Kiralama admin — silme");
        putRentQr(
                "RENT_MANAGER_READ",
                "RENT_MANAGER_UPDATE",
                "RENT_MANAGER_WRITE",
                "RENT_MANAGER_DELETE",
                "Kiralama manager — okuma",
                "Kiralama manager — güncelleme",
                "Kiralama manager — oluşturma",
                "Kiralama manager — silme");
        putRentQr(
                "RENT_USER_READ",
                "RENT_USER_UPDATE",
                "RENT_USER_WRITE",
                "RENT_USER_DELETE",
                "Kiralama kullanıcı — okuma",
                "Kiralama kullanıcı — güncelleme",
                "Kiralama kullanıcı — oluşturma",
                "Kiralama kullanıcı — silme");
        putRentQr(
                "QR_ADMIN_READ",
                "QR_ADMIN_UPDATE",
                "QR_ADMIN_WRITE",
                "QR_ADMIN_DELETE",
                "QR admin — okuma",
                "QR admin — güncelleme",
                "QR admin — oluşturma",
                "QR admin — silme");
        putRentQr(
                "QR_MANAGER_READ",
                "QR_MANAGER_UPDATE",
                "QR_MANAGER_WRITE",
                "QR_MANAGER_DELETE",
                "QR manager — okuma",
                "QR manager — güncelleme",
                "QR manager — oluşturma",
                "QR manager — silme");
        putRentQr(
                "QR_USER_READ",
                "QR_USER_UPDATE",
                "QR_USER_WRITE",
                "QR_USER_DELETE",
                "QR kullanıcı — okuma",
                "QR kullanıcı — güncelleme",
                "QR kullanıcı — oluşturma",
                "QR kullanıcı — silme");
    }

    private static void putRentQr(
            String c1, String c2, String c3, String c4, String d1, String d2, String d3, String d4) {
        AUTHORITY_DESCRIPTIONS.put(c1, d1);
        AUTHORITY_DESCRIPTIONS.put(c2, d2);
        AUTHORITY_DESCRIPTIONS.put(c3, d3);
        AUTHORITY_DESCRIPTIONS.put(c4, d4);
    }

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        AUTHORITY_DESCRIPTIONS.forEach(this::ensureAuthority);
        seedRentRoles();
        seedQrRoles();
    }

    private void ensureAuthority(String code, String description) {
        authorityRepository
                .findByCode(code)
                .orElseGet(
                        () ->
                                authorityRepository.save(
                                        Authority.builder().code(code).description(description).build()));
    }

    private void seedRentRoles() {
        ensureServiceRole(
                RentRole.RENT_ADMIN.name(),
                "Kiralama — admin",
                RbacReferenceCatalog.SERVICE_RENT,
                Stream.of(RentAdminAuthority.values()).map(Enum::name).toList());
        ensureServiceRole(
                RentRole.RENT_MANAGER.name(),
                "Kiralama — manager",
                RbacReferenceCatalog.SERVICE_RENT,
                Stream.of(RentManagerAuthority.values()).map(Enum::name).toList());
        ensureServiceRole(
                RentRole.RENT_USER.name(),
                "Kiralama — kullanıcı",
                RbacReferenceCatalog.SERVICE_RENT,
                Stream.of(RentUserAuthority.values()).map(Enum::name).toList());
    }

    private void seedQrRoles() {
        ensureServiceRole(
                QrRole.QR_ADMIN.name(),
                "QR — admin",
                RbacReferenceCatalog.SERVICE_QR,
                Stream.of(QrAdminAuthority.values()).map(Enum::name).toList());
        ensureServiceRole(
                QrRole.QR_MANAGER.name(),
                "QR — manager",
                RbacReferenceCatalog.SERVICE_QR,
                Stream.of(QrManagerAuthority.values()).map(Enum::name).toList());
        ensureServiceRole(
                QrRole.QR_USER.name(),
                "QR — kullanıcı",
                RbacReferenceCatalog.SERVICE_QR,
                Stream.of(QrUserAuthority.values()).map(Enum::name).toList());
    }

    private void ensureServiceRole(String roleCode, String roleName, String serviceCode, List<String> authorityCodes) {
        Role role =
                roleRepository
                        .findByCodeWithAuthorities(roleCode)
                        .orElseGet(
                                () ->
                                        roleRepository.save(
                                                Role.builder()
                                                        .code(roleCode)
                                                        .name(roleName)
                                                        .serviceCode(serviceCode)
                                                        .authorities(new HashSet<>())
                                                        .build()));

        boolean dirty = false;
        if (!Objects.equals(role.getName(), roleName)) {
            role.setName(roleName);
            dirty = true;
        }
        if (!Objects.equals(role.getServiceCode(), serviceCode)) {
            role.setServiceCode(serviceCode);
            dirty = true;
        }
        if (role.getAuthorities() == null) {
            role.setAuthorities(new HashSet<>());
            dirty = true;
        }

        for (String ac : authorityCodes) {
            Authority auth =
                    authorityRepository.findByCode(ac).orElseThrow(() -> new IllegalStateException("Yetki eksik: " + ac));
            boolean has =
                    role.getAuthorities().stream().anyMatch(a -> Objects.equals(a.getCode(), ac));
            if (!has) {
                role.getAuthorities().add(auth);
                dirty = true;
            }
        }
        if (dirty) {
            roleRepository.save(role);
        }
    }
}
