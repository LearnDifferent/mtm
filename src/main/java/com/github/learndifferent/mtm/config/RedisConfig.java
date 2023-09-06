package com.github.learndifferent.mtm.config;

import com.github.learndifferent.mtm.exception.ServiceException;
import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
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

    @Bean(name = "springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return RedisSerializer.json();
    }

    @Bean
    public RedisCacheConfiguration defaultCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                // the default prefix is cacheName followed by double colons, change it to cacheName only
                // Default: @CachePut(value = "cacheName", key = ":key") -> cacheName:::key
                // Now: @CachePut(value = "cacheName", key = ":key") -> cacheName:key
                .computePrefixWith(name -> name)
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
        Map<String, Long> configs = redisConfigProperties.getCacheConfigs();
        Map<String, RedisCacheConfiguration> cacheConfigs = transferToCacheConfigs(configs);

        return builder -> builder.withInitialCacheConfigurations(cacheConfigs);
    }

    private Map<String, RedisCacheConfiguration> transferToCacheConfigs(Map<String, Long> configs) {
        return configs.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofSeconds(entry.getValue())),
                        (a, b) -> {
                            throw new ServiceException("Key Conflicts");
                        }));
    }
}