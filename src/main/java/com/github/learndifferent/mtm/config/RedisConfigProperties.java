package com.github.learndifferent.mtm.config;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Redis Configuration Properties
 *
 * @author zhou
 * @date 2022/4/4
 */
@Configuration
@ConfigurationProperties(prefix = "custom-redis")
public class RedisConfigProperties {

    private String host;
    private int port;
    private Map<String, Long> cacheConfigs;
    private Map<String, Map<String, Long>> keyConstantsAndCacheConfigs;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setCacheConfigs(Map<String, Map<String, Long>> keyConstantsAndCacheConfigs) {
        this.keyConstantsAndCacheConfigs = keyConstantsAndCacheConfigs;

        // Extract the Map<String, Long> (value) from the Map<String, Map<String, Long>>
        Collection<Map<String, Long>> configPropertiesValues = keyConstantsAndCacheConfigs.values();
        this.cacheConfigs =
                configPropertiesValues
                        .stream()
                        .flatMap(innerMap -> innerMap.entrySet().stream())
                        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public Map<String, Long> getCacheConfigs() {
        return cacheConfigs;
    }

    public Map<String, Map<String, Long>> getKeyConstantsAndCacheConfigs() {
        return this.keyConstantsAndCacheConfigs;
    }
}