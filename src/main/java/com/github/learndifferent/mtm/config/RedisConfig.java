package com.github.learndifferent.mtm.config;

import java.time.Duration;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Redis Configuration
 *
 * @author zhou
 * @date 2021/09/05
 */
@Configuration
@EnableCaching
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1200)
public class RedisConfig {

//    @Bean
//    public static ConfigureRedisAction configureRedisAction() {
//        return ConfigureRedisAction.NO_OP;
//    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("mid", 6379));
    }

    /**
     * Use {@link GenericJackson2JsonRedisSerializer} as serializer
     *
     * @return {@code RedisSerializer<Object>} serializer
     */
    @Bean(name = "springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return RedisSerializer.json();
    }

    @Bean
    public RedisCacheConfiguration defaultCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(1));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration("user:name",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(10L)))
                .withCacheConfiguration("tag:all",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(10L)))
                .withCacheConfiguration("tag:popular",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(10L)))
                .withCacheConfiguration("user:all",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(20L)))
                .withCacheConfiguration("bookmarks:visited",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(30L)))
                .withCacheConfiguration("system-log",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(40L)))
                .withCacheConfiguration("tag:a",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ZERO));
    }
}