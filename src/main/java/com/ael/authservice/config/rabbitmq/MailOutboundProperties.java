package com.ael.authservice.config.rabbitmq;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.mail.rabbit")
public class MailOutboundProperties {
    @NotBlank
    private String exchange = "algorycode.mail.exchange";
    @NotBlank
    private String routingKey = "mail.send";
}
