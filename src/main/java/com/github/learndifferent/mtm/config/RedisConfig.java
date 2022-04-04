package com.github.learndifferent.mtm.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final RedisConfigProperties redisConfigProperties;

    @Autowired
    public RedisConfig(RedisConfigProperties redisConfigProperties) {
        this.redisConfigProperties = redisConfigProperties;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        String host = redisConfigProperties.getHost();
        int port = redisConfigProperties.getPort();
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
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
        return builder -> builder.withInitialCacheConfigurations(getCustomCacheConfigurations());
    }

    private Map<String, RedisCacheConfiguration> getCustomCacheConfigurations() {
        float expectedSize = 8;
        int initialCapacity = (int) (expectedSize / 0.75F + 1.0F);

        Map<String, Long> configs = new HashMap<>(initialCapacity);
        configs.put("comment:count", 10L);
        configs.put("user:name", 10L);
        configs.put("tag:all", 10L);
        configs.put("tag:popular", 10L);
        configs.put("user:all", 20L);
        configs.put("bookmarks:visited", 30L);
        configs.put("system-log", 40L);
        configs.put("tag:a", 0L);
        return getCustomCacheConfigurations(configs);
    }

    Map<String, RedisCacheConfiguration> getCustomCacheConfigurations(Map<String, Long> configs) {
        int expectedSize = configs.size();
        int initialCapacity = (int) ((float) expectedSize / 0.75F + 1.0F);

        Map<String, RedisCacheConfiguration> result = new HashMap<>(initialCapacity);
        configs.forEach((name, ttl) ->
                result.put(name, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(ttl))));
        return result;
    }

//    @Bean
//    public static ConfigureRedisAction configureRedisAction() {
//        return ConfigureRedisAction.NO_OP;
//    }
}