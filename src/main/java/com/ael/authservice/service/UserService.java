package com.ael.authservice.service;

import com.ael.authservice.dto.request.MyProfilePatchRequest;
import com.ael.authservice.dto.request.RegistrationRole;
import com.ael.authservice.dto.response.MyProfileResponse;
import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.mapper.UserMapper;
import com.ael.authservice.model.Role;
import com.ael.authservice.model.User;
import com.ael.authservice.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationRoleResolver registrationRoleResolver;

    /** Google vb. otomatik ilk kayıt: varsayılan {@link RegistrationRole#USER}. */
    @Transactional
    public UserResponse createUser(User user) {
        return registerNewUser(user, RegistrationRole.USER);
    }

    /**
     * Yeni kullanıcıyı şifre hashleyerek kaydeder ve {@link RegistrationRole} ile rol atar.
     */
    @Transactional
    public UserResponse registerNewUser(User user, RegistrationRole registrationRole) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(null);
        user.setRoles(null);
        Role role = registrationRoleResolver.resolve(registrationRole);
        user.setRole(role);
        user.setRoles(role.getCode());
        return userMapper.toResponse(userRepository.save(user));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Kayıtlı kullanıcı e-postası var mı (büyük/küçük harf duyarsız).
     * Misafir rezervasyon akışında kullanılır.
     */
    public boolean isEmailRegistered(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        return userRepository.existsByEmailIgnoreCase(normalized);
    }

    public Optional<UserResponse> findUserByEmail(String email) {
        return userRepository.findByEmailWithRoleAndAuthorities(email).map(userMapper::toResponse);
    }

    public UserResponse getUserById(Integer userId) {
        return userRepository
                .findByIdWithRoleAndAuthorities(userId)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
    }

    /** İlk açılışta created_at boşsa doldurulur (eski kayıtlar). */
    @Transactional
    public MyProfileResponse getMyProfile(Integer userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
        ensureCreatedAt(user);
        return toMyProfileResponse(user);
    }

    @Transactional
    public MyProfileResponse patchMyProfile(Integer userId, MyProfilePatchRequest body) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
        ensureCreatedAt(user);

        if (body.getFirstName() != null) {
            user.setFirstName(body.getFirstName().trim());
        }
        if (body.getLastName() != null) {
            user.setLastName(body.getLastName().trim());
        }
        if (body.getEmail() != null) {
            String nextEmail = body.getEmail().trim();
            userRepository
                    .findByEmail(nextEmail)
                    .filter(u -> !u.getUserId().equals(userId))
                    .ifPresent(u -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu e-posta başka bir hesapta kayıtlı");
                    });
            user.setEmail(nextEmail);
        }
        if (body.getPhoneNumber() != null) {
            user.setPhoneNumber(body.getPhoneNumber().trim());
        }
        if (body.getNotifyEmailImportant() != null) {
            user.setNotifyEmailImportant(body.getNotifyEmailImportant());
        }
        if (body.getNotifyScanAlerts() != null) {
            user.setNotifyScanAlerts(body.getNotifyScanAlerts());
        }
        if (body.getNotifyWeeklyReport() != null) {
            user.setNotifyWeeklyReport(body.getNotifyWeeklyReport());
        }
        if (body.getNotifyMarketingEmails() != null) {
            user.setNotifyMarketingEmails(body.getNotifyMarketingEmails());
        }
        if (body.getNotifyPushBrowser() != null) {
            user.setNotifyPushBrowser(body.getNotifyPushBrowser());
        }

        return toMyProfileResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(Integer userId, String currentPassword, String newPassword) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));

        String stored = user.getPassword();
        if (stored == null
                || stored.isBlank()
                || "GOOGLE_AUTH".equalsIgnoreCase(stored.trim())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bu hesap e-posta/şifre ile giriş kullanmıyor; şifre değiştirilemez");
        }

        if (!passwordEncoder.matches(currentPassword, stored)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Şifre değişikliği yapılamadı");
        }

        if (passwordEncoder.matches(newPassword, stored)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Şifre değişikliği yapılamadı");
        }

        String encoded = passwordEncoder.encode(newPassword);
        user.setPassword(encoded);
        userRepository.save(user);
        userRepository.flush();

        User persisted = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Kullanıcı yeniden yüklenemedi"));
        if (!passwordEncoder.matches(newPassword, persisted.getPassword())) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Şifre güncellemesi doğrulanamadı");
        }
    }

    private void ensureCreatedAt(User user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    private static MyProfileResponse toMyProfileResponse(User user) {
        return MyProfileResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .twoFactorEnabled(user.isTwoFactorEnabled())
                .memberSince(user.getCreatedAt())
                .notifyEmailImportant(user.isNotifyEmailImportant())
                .notifyScanAlerts(user.isNotifyScanAlerts())
                .notifyWeeklyReport(user.isNotifyWeeklyReport())
                .notifyMarketingEmails(user.isNotifyMarketingEmails())
                .notifyPushBrowser(user.isNotifyPushBrowser())
                .build();
    }
}

