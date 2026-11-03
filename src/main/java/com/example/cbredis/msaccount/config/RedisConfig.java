package com.example.cbredis.msaccount.config;

import com.example.cbredis.msaccount.domain.AccountBalance;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, AccountBalance> accountBalanceRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {

        Jackson2JsonRedisSerializer<AccountBalance> valueSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, AccountBalance.class);

        RedisSerializationContext<String, AccountBalance> context =
                RedisSerializationContext.<String, AccountBalance>newSerializationContext(
                                new StringRedisSerializer())
                        .key(new StringRedisSerializer())
                        .value(valueSerializer)
                        .hashKey(new StringRedisSerializer())
                        .hashValue(valueSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}
