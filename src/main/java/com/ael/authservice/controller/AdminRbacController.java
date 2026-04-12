package com.ael.authservice.controller;

import com.ael.authservice.dto.admin.AssignUserRoleRequest;
import com.ael.authservice.dto.admin.AuthorityView;
import com.ael.authservice.dto.admin.GrantRoleAuthorityRequest;
import com.ael.authservice.dto.admin.RoleDetailView;
import com.ael.authservice.service.RbacAdminService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rol–yetki ve kullanıcı–rol yönetimi. Varsayılan olarak yalnızca access token içinde {@code RBAC_MANAGE}
 * yetkisi olan çağrılar kabul edilir ({@link com.ael.authservice.security.AdminApiAuthorizationFilter}).
 */
@RestController
@RequestMapping("/admin/rbac")
@RequiredArgsConstructor
public class AdminRbacController {

    private final RbacAdminService rbacAdminService;

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDetailView>> listRoles(@RequestParam(required = false) String service) {
        return ResponseEntity.ok(rbacAdminService.listRoles(service));
    }

    @GetMapping("/roles/{roleCode}")
    public ResponseEntity<RoleDetailView> getRole(@PathVariable String roleCode) {
        return ResponseEntity.ok(rbacAdminService.getRole(roleCode));
    }

    @GetMapping("/authorities")
    public ResponseEntity<List<AuthorityView>> listAuthorities() {
        return ResponseEntity.ok(rbacAdminService.listAuthorities());
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<Void> assignOrChangeUserRole(
            @PathVariable int userId, @Valid @RequestBody AssignUserRoleRequest body) {
        rbacAdminService.assignUserRole(userId, body.roleCode());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/roles/{roleCode}/authorities")
    public ResponseEntity<RoleDetailView> grantAuthority(
            @PathVariable String roleCode, @Valid @RequestBody GrantRoleAuthorityRequest body) {
        return ResponseEntity.ok(rbacAdminService.grantAuthorityToRole(roleCode, body.authorityCode()));
    }

    @DeleteMapping("/roles/{roleCode}/authorities/{authorityCode}")
    public ResponseEntity<RoleDetailView> revokeAuthority(
            @PathVariable String roleCode, @PathVariable String authorityCode) {
        return ResponseEntity.ok(rbacAdminService.revokeAuthorityFromRole(roleCode, authorityCode));
    }
}
