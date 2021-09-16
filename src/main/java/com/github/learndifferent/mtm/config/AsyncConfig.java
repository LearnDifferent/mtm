package com.github.learndifferent.mtm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步配置
 *
 * @author zhou
 * @date 2021/09/05
 */
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Value("${custom-async.core-pool-size}")
    private int corePoolSize;

    @Value("${custom-async.alive-seconds}")
    private int aliveSeconds;

    @Value("${custom-async.queue-capacity}")
    private int queueCapacity;

    @Bean("asyncTaskExecutor")
    @Override
    public Executor getAsyncExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        executor.setKeepAliveSeconds(aliveSeconds);
        executor.setQueueCapacity(queueCapacity);
        // 这里处理的任务不重要，可以允许丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.setThreadNamePrefix("custom-async-");
        executor.initialize();
        return executor;
    }

}
