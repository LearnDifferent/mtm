package com.github.learndifferent.mtm.config;

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
}