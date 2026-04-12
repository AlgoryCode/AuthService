package com.ael.authservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Cloud Gateway zaten CORS ekler; burada da açık olursa yanıtta çift
 * {@code Access-Control-Allow-Origin} oluşur ve tarayıcı “CORS error” verir (200 olsa bile).
 * Gateway arkasında {@code authservice.cors.enabled=false} (varsayılan). Doğrudan 8099’a
 * tarayıcıdan istek atıyorsan {@code true} yap.
 */
@Configuration
@ConditionalOnProperty(prefix = "authservice.cors", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}