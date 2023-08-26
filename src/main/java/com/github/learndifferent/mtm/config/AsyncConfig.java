package com.github.learndifferent.mtm.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Async Configuration
 *
 * @author zhou
 * @date 2021/09/05
 */
@EnableAsync
@EnableConfigurationProperties(AsyncConfigProperties.class)
@Configuration
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    private final AsyncConfigProperties asyncConfigProperties;

    @Bean("asyncTaskExecutor")
    @Override
    public Executor getAsyncExecutor() {

        int corePoolSize = asyncConfigProperties.getCorePoolSize();
        int aliveSeconds = asyncConfigProperties.getAliveSeconds();
        int queueCapacity = asyncConfigProperties.getQueueCapacity();
        int maxPoolSize = Runtime.getRuntime().availableProcessors();
        if (maxPoolSize < corePoolSize) {
            corePoolSize = maxPoolSize - 1;
        }
        if (corePoolSize == 0) {
            corePoolSize = 1;
        }

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setKeepAliveSeconds(aliveSeconds);
        executor.setQueueCapacity(queueCapacity);
        // Use Discard Policy because this is built for unimportant tasks
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.setThreadNamePrefix("custom-async-");
        executor.initialize();
        return executor;
    }

}
