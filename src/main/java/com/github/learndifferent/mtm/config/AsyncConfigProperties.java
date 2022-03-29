package com.github.learndifferent.mtm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Async Configuration Properties
 *
 * @author zhou
 * @date 2022/3/28
 */
@Configuration
@ConfigurationProperties(prefix = "custom-async")
public class AsyncConfigProperties {

    private int corePoolSize;
    private int aliveSeconds;
    private int queueCapacity;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getAliveSeconds() {
        return aliveSeconds;
    }

    public void setAliveSeconds(int aliveSeconds) {
        this.aliveSeconds = aliveSeconds;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
}
