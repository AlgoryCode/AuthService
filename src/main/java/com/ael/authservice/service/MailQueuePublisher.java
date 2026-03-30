package com.ael.authservice.service;


import com.ael.authservice.config.rabbitmq.MailOutboundProperties;

import com.ael.authservice.config.rabbitmq.MailQueueMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailQueuePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MailOutboundProperties mailOutbound;

    public void publish(MailQueueMessage message) {
        rabbitTemplate.convertAndSend(
                mailOutbound.getExchange(),
                mailOutbound.getRoutingKey(),
                message);
        log.debug("Mail queued: to={}, subject={}", message.getTo(), message.getSubject());
    }
}