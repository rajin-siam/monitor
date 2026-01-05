package com.energy.monitor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis")
public record RedisConfigProperties(
        String host,
        int port,
        String username,
        String password,
        int database
) {
}