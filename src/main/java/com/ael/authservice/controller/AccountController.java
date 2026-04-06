package com.ael.authservice.controller;

import com.ael.authservice.dto.request.ChangePasswordRequest;
import com.ael.authservice.dto.request.MyProfilePatchRequest;
import com.ael.authservice.dto.response.MyProfileResponse;
import com.ael.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @GetMapping("/myprofile")
    public ResponseEntity<MyProfileResponse> myProfile(@RequestHeader("X-User-Id") String userIdHeader) {
        Integer userId = Integer.parseInt(userIdHeader.trim());
        return ResponseEntity.ok(userService.getMyProfile(userId));
    }

    @PatchMapping("/myprofile")
    public ResponseEntity<MyProfileResponse> patchMyProfile(
            @RequestHeader("X-User-Id") String userIdHeader,
            @Valid @RequestBody MyProfilePatchRequest body) {
        Integer userId = Integer.parseInt(userIdHeader.trim());
        return ResponseEntity.ok(userService.patchMyProfile(userId, body));
    }

    /** Oturum açık kullanıcı mevcut şifresiyle yeni şifre belirler. Gateway: JWT + X-User-Id. */
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader("X-User-Id") String userIdHeader,
            @Valid @RequestBody ChangePasswordRequest body) {
        Integer userId = Integer.parseInt(userIdHeader.trim());
        userService.changePassword(userId, body.getCurrentPassword(), body.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}
