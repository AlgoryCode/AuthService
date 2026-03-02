package com.ael.authservice.service;


import com.ael.authservice.model.UserSessionLog;
import com.ael.authservice.repository.IUserSessionLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserSessionLogService {
    private final IUserSessionLog userSession;


    public UserSessionLog createSessionLog(UserSessionLog userSessionLog) {
       return userSession.save(userSessionLog);
    }

    public void revokeUserSession(String sessionId, Integer userId) {

    }

    public List<UserSessionLog> getActiveUserSessions(Integer userId) {
        return null;
    }


    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // X-Forwarded-For birden fazla IP içerebilir, ilkini al
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        if (xForwardedProto != null && !xForwardedProto.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedProto)) {
            return xForwardedProto;
        }

        String cfConnectingIp = request.getHeader("CF-Connecting-IP");
        if (cfConnectingIp != null && !cfConnectingIp.isEmpty() && !"unknown".equalsIgnoreCase(cfConnectingIp)) {
            return cfConnectingIp;
        }

        // Son çare olarak remote address'i kullan
        return request.getRemoteAddr();
    }


    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }


    private String extractDeviceInfo(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown Device";
        }

        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "Mobile Device";
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            return "Tablet Device";
        } else if (userAgent.contains("windows")) {
            return "Windows Desktop";
        } else if (userAgent.contains("mac")) {
            return "Mac Desktop";
        } else if (userAgent.contains("linux")) {
            return "Linux Desktop";
        } else {
            return "Unknown Desktop";
        }
    }


}
