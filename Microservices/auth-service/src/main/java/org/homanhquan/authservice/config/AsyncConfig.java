package org.homanhquan.authservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Getter
@Setter
@Configuration
/**
 * @Value: Injects a single property from application.properties or application.yml.
 * @ConfigurationProperties:
 * - Binds a group of related properties (with a common prefix) into a class.
 * - It also offers useful features like type safety, validation (@Validated + JSR-303), and IDE auto-completion (async.executor.core-pool-size = private int corePoolSize).
 */
@ConfigurationProperties(prefix = "async.executor")
@EnableAsync // To enable @Async support
public class AsyncConfig implements AsyncConfigurer {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private String threadNamePrefix;
    private int keepAliveSeconds;
    private boolean allowCoreThreadTimeout;
    private int awaitTerminationSeconds;

    /**
     * Bean name "taskExecutor" allows @Async to use this executor implicitly.
     * If you create a bean with a different name (e.g., "myExecutor"), then you must use @Async("myExecutor").
     */
    @Bean(name = "taskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = getThreadPoolTaskExecutor();
        executor.initialize();
        return executor;
    }

    /**
     * The ranges of these values depend on each team’s decision for each product.
     * Explanation for each field:
     * - corePoolSize: Minimum of 5 threads available to process tasks.
     * - maxPoolSize: When the 5 core threads are all busy AND the queue is full, the pool can expand up to 10 threads.
     * - queueCapacity: If all 5 core threads are busy, incoming tasks will be placed into the queue, up to 100 tasks.
     * - threadNamePrefix: Prefix used for naming threads in the thread pool.
     * - keepAliveSeconds & allowCoreThreadTimeout: Idle threads will be terminated after 1 minute.
     * - CallerRunsPolicy(): If all 10 threads are busy and the queue is full, the task will be executed by the caller thread.
     * - awaitTerminationSeconds: When Spring shuts down, the executor will stop accepting new tasks
     *   and will wait for running tasks to finish. If everything completes early, the app stops earlier;
     *   Otherwise it will wait up to 1 minute before forcing shutdown.
     */
    private ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setAllowCoreThreadTimeOut(allowCoreThreadTimeout);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        return executor;
    }
}
