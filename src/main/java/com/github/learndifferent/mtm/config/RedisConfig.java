package com.github.learndifferent.mtm.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
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
@Slf4j
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisConfigProperties redisConfigProperties;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        String host = redisConfigProperties.getHost();
        int port = redisConfigProperties.getPort();
        log.info("Redis Host: {}, Port: {}", host, port);
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }

    @Bean(name = "springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        log.info("Spring Session Default Redis Serializer is created");
        return RedisSerializer.json();
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(keySerializer());
        template.setValueSerializer(valueSerializer());

        template.setHashKeySerializer(keySerializer());
        template.setHashValueSerializer(valueSerializer());

        template.afterPropertiesSet();

        log.info("RedisTemplate is created: {}", template);
        return template;
    }

    @Bean
    public StringRedisSerializer keySerializer() {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        log.info("KeySerializer is created: {}", stringRedisSerializer);
        return stringRedisSerializer;
    }

    @Bean
    public RedisSerializer<Object> valueSerializer() {
        Jackson2JsonRedisSerializer<Object> serializer =
                new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // This configuration is required; otherwise, if there are objects within the serialized object,
        // the following error will be thrown:
        // java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);

        serializer.setObjectMapper(objectMapper);

        log.info("ValueSerializer is created: {}", serializer);
        return serializer;
    }

    @Bean
    public RedisCacheConfiguration defaultCacheConfiguration() {
        return RedisCacheConfiguration
                // Use the current thread classloader as the classloader
                .defaultCacheConfig(Thread.currentThread().getContextClassLoader())
                // the default prefix is cacheName followed by double colons, change it to cacheName only
                // Default: @CachePut(value = "cacheName", key = "key") -> cacheName::key
                // Now: @CachePut(value = "cacheName", key = "key") -> cacheName:key
                .computePrefixWith(name -> name + ":")
                .serializeKeysWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(keySerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(valueSerializer())
                )
                .entryTtl(Duration.ofMinutes(1));
    }

    @Bean
    public CacheManager redisCacheManager(LettuceConnectionFactory connectionFactory) {
        Map<String, Long> configs = redisConfigProperties.getCacheConfigs();
        Map<String, RedisCacheConfiguration> initialCacheConfigs = transferToCacheConfigs(configs);

        RedisCacheConfiguration defaultConfig = defaultCacheConfiguration();

        RedisCacheManager manager = RedisCacheManager
                .builder(connectionFactory)
                // default config
                .cacheDefaults(defaultConfig)
                // special config
                .withInitialCacheConfigurations(initialCacheConfigs)
                .build();

        log.info("Initial Redis Cache Manager: {}, Default Cache Config: {}, Initial Cache Configs: {}",
                manager,
                defaultConfig,
                initialCacheConfigs);

        return manager;
    }

    private Map<String, RedisCacheConfiguration> transferToCacheConfigs(Map<String, Long> configs) {
        return configs
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                // key
                                Entry::getKey,
                                // value (RedisCacheConfiguration)
                                entry ->
                                        // get the default config
                                        defaultCacheConfiguration()
                                                // override the default ttl
                                                .entryTtl(Duration.ofSeconds(entry.getValue())),
                                (k1, k2) -> {
                                    log.warn("Key Conflicts: {} -> {}, use the first one", k1, k2);
                                    return k1;
                                }
                        )
                );
    }
}