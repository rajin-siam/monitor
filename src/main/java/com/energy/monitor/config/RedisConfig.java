package com.energy.monitor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableConfigurationProperties(RedisConfigProperties.class)
public class RedisConfig {

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration(@NotNull RedisConfigProperties properties) {
        var config = new RedisStandaloneConfiguration();
        config.setHostName(properties.host());
        config.setPort(properties.port());
        if (Strings.isNotBlank(properties.username())) {
            config.setUsername(properties.username());
        }
        if (Strings.isNotBlank(properties.password())) {
            config.setPassword(properties.password());
        }
        config.setDatabase(properties.database());
        return config;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisStandaloneConfiguration redisConfig) {
        return new JedisConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory);
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public JedisPool jedisPool(RedisConfigProperties redisConfig) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setJmxEnabled(false);
        poolConfig.setMaxTotal(100);

        if (Strings.isNotBlank(redisConfig.username()) && Strings.isNotBlank(redisConfig.password())) {
            return new JedisPool(
                    poolConfig,
                    redisConfig.host(),
                    redisConfig.port(),
                    2000,
                    redisConfig.username(),
                    redisConfig.password(),
                    redisConfig.database()
            );
        }
        if (Strings.isNotBlank(redisConfig.password())) {
            return new JedisPool(
                    poolConfig,
                    redisConfig.host(),
                    redisConfig.port(),
                    2000,
                    redisConfig.password(),
                    redisConfig.database()
            );
        }
        return new JedisPool(
                poolConfig,
                redisConfig.host(),
                redisConfig.port()
        );
    }
}