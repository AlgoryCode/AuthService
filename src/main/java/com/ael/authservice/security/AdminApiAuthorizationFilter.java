package com.ael.authservice.security;

import com.ael.authservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * {@code /admin/**} yollarında Bearer access token doğrular ve {@code RBAC_MANAGE} yetkisini arar.
 * Yerelde korumayı kapatmak için {@code app.security.admin-api.protected=false}.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
@RequiredArgsConstructor
public class AdminApiAuthorizationFilter extends OncePerRequestFilter {

    public static final String REQUIRED_AUTHORITY = "RBAC_MANAGE";

    private final JwtUtil jwtUtil;

    @Value("${app.security.admin-api.protected:true}")
    private boolean adminApiProtected;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        if (!adminApiProtected) {
            return true;
        }
        return !request.getRequestURI().startsWith("/admin/");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bearer token gerekli");
            return;
        }
        String token = header.substring(7).trim();
        if (!Boolean.TRUE.equals(jwtUtil.validateToken(token))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Geçersiz veya süresi dolmuş token");
            return;
        }
        if (!jwtUtil.extractAuthorities(token).contains(REQUIRED_AUTHORITY)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "RBAC_MANAGE yetkisi gerekli");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
