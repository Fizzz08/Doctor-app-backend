package com.example.DemoProject.Register.config;

import ch.qos.logback.classic.pattern.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Queue;

@Configuration
public class rabbitMQConfig {
    public static final String QUEUE_NAME = "otp_email_queue";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true); // true = durable (survives restart)
    }

    // This converts your Java objects into JSON automatically
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
