package com.ael.authservice.util;

import com.ael.authservice.dto.response.UserResponse;
import com.ael.authservice.dto.response.RefreshTokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${spring.security.jwt.secret}")
    private String secret;

    @Value("${spring.security.jwt.expiration}")
    private Long accessExpiration;

    @Value("${spring.security.jwt.refresh-expiration}")
    private Long refreshExpiration;

    private SecretKey getSigningKey() {
        // Convert hex string to byte array
        byte[] keyBytes = hexStringToByteArray(secret.trim());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public String generateAccessToken(UserResponse user,String uuid) {
        String jti = UUID.randomUUID().toString();
        String sessionId = UUID.randomUUID().toString();

        Map<String, Object> claims = new HashMap<>();
        claims.put("jti", jti);
        claims.put("session_id",sessionId);
        //claims.put("role", user.getRoles());
        claims.put("userId", user.getUserId());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getFamilyName());
        claims.put("tokenType", "ACCESS_TOKEN");
        return createAccessToken(claims, user.getUserId().toString());
    }

    public RefreshTokenResponse generateRefreshToken(String uuid) {
        String jti = UUID.randomUUID().toString();
        Map<String, Object> claims = new HashMap<>();
        claims.put("jti", jti);
        claims.put("type", "REFRESH_TOKEN");
        claims.put("session_id",uuid);
        log.info("Refresh Token generated successfully.\n");
        String refreshToken = createRefreshToken(claims);


        return RefreshTokenResponse.builder()
                .refreshToken(refreshToken)
                .refreshTokenId(jti)
                .expiryDate(LocalDateTime.now().plus(refreshExpiration, ChronoUnit.MILLIS))
                .build();
    }

    public RefreshTokenResponse generateRefreshToken(String uuid,Date date) {
        String jti = UUID.randomUUID().toString();
        Map<String, Object> claims = new HashMap<>();
        claims.put("jti", jti);
        claims.put("type", "REFRESH_TOKEN");
        log.info("Refresh Token generated successfully.\n");
        String refreshToken = createRefreshToken(claims);


        return RefreshTokenResponse.builder()
                .refreshToken(refreshToken)
                .refreshTokenId(jti)
                .expiryDate(LocalDateTime.now().plus(refreshExpiration, ChronoUnit.MILLIS))
                .build();
    }


    private String createAccessToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String createRefreshToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }



    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Integer extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Integer.class));
    }

    public String extractUUID(String token) {
        return extractClaim(token, claims -> claims.get("UUID", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractSessionIdFromToken(String token) {
        try {
            // Expired token'ları da parse et
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .setAllowedClockSkewSeconds(86400) // 24 saat tolerans
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("session_id", String.class);
        } catch (Exception e) {
            log.error("Failed to extract session_id from expired token: {}", e.getMessage());
            return null;
        }
    }

    public String generateCsrfToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }


}