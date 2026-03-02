package com.ael.authservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    private static final String BLOCKLIST_PREFIX = "revokedRefreshTokenList:";





    public void addRevokedUUIDtoBlocList(String uuid, long ttlSeconds) {
        if (uuid == null || ttlSeconds <= 0) {
            return;
        }

        try {
            String key = BLOCKLIST_PREFIX + uuid;
            redisTemplate.opsForValue().set(key, "revoked", ttlSeconds, TimeUnit.SECONDS);
            log.info("UUID added to blocklist: {}, TTL: {} seconds", uuid, ttlSeconds);
        } catch (Exception e) {
            log.error("Failed to add UUID to blocklist: {}", e.getMessage());
        }
    }

    public boolean isBlocked(String uuid) {
        if (uuid == null) {
            return false;
        }

        try {
            String key = BLOCKLIST_PREFIX + uuid;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Failed to check blocklist: {}", e.getMessage());
            return false; // Redis hatası durumunda geçerli say
        }
    }


    public void removeFromBlocklist(String uuid) {
        try {
            String key = BLOCKLIST_PREFIX + uuid;
            redisTemplate.delete(key);
            log.info("UUID removed from blocklist: {}", uuid);
        } catch (Exception e) {
            log.error("Failed to remove from blocklist: {}", e.getMessage());
        }
    }



}
