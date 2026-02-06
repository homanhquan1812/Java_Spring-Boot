package org.homanhquan.productservice.config;

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

/**
 * Thread definition:
 * - Thread is the smallest unit of execution inside a process. In Java, when you run a program, the JVM creates at least one main thread to execute the main() method.
 * - There are 2 types of Thread:
 *   + Single-thread: Only one task runs at a time, step by step.
 *   + Multi-thread: Multiple tasks can run concurrently (CPU switches between them or runs them on multiple cores).
 * - There are two main ways to create a thread:
 *   + Extend the Thread class and override the run() method.
 *   + Implement the Runnable interface and pass it to a Thread object.
 *   run() is an abstract method that you must implement in Thread or Runnable.
 * Comparisons between Thread, Runnable, Callable, ExecutorService:
 * - Thread: Represents an actual thread of execution. Not recommended in production due to tight coupling and single inheritance limitation.
 * - Runnable: Represents a task (Unit of work), not a thread. It separates task logic from thread management, and doesn't return a value or throw checked exceptions.
 * - Callable: Similar to Runnable, but can return a value and throw checked exceptions. Used with Future.
 *   Future: An interface representing the result of an asynchronous task.
 * - ExecutorService: Manages threads using a thread pool. Decouples task submission from thread execution. Recommended for production systems.
 * Thread lifecycles: NEW (newly created) → RUNNABLE (ready to run) → BLOCKED/WAITING/TIMED_WAITING (waiting for resources) → TERMINATED (finished).
 * ==================================================
 * Key concepts of Thread:
 * - Race Condition: Two or more threads access shared data at the same time, leading to unpredictable results.
 * - Deadlock: Two or more threads are waiting for each other’s locks, and none can proceed.
 * - Starvation: A thread never gets CPU time or resources because others dominate them.
 * - Livelock: Threads keep responding to each other’s state but no progress is made (like two people both stepping aside repeatedly).
 * - synchronized: Keyword to ensure only one thread can access a method/block at a time (thread-safe).
 * - volatile: Keyword ensuring a variable's value is always read from main memory (visibility guarantee).
 * - Lock (ReentrantLock): More flexible than synchronized, supports try-lock and timed lock.
 * - Atomic Classes (AtomicInteger, AtomicBoolean): Lock-free thread-safe operations for single variables, better performance than synchronized for simple counters.
 * ==================================================
 * Concurrency & Parallelism definition:
 * - Concurrency refers to the ability of a program to perform multiple tasks simultaneously or manage multiple tasks by rapidly switching between them.
 *   Ex: [Core 1] -> Task A (switch) Task B (switch) Task A (switch) Task B -> Done (5s).
 * - Parallelism refers to the ability of a program to execute multiple tasks at the same time by utilizing multiple processors or CPU cores concurrently,
 *   so that tasks literally run in parallel rather than just switching between them.
 *   Ex: [Core 1] -> Task A -> Done (2.5s), [Core 2] -> Task B -> Done (2.5s).
 * ==================================================
 * Annotation explanation:
 * - @Getter: Generates getter methods for all fields.
 * - @Configuration: Marks a class as a source of bean definitions.
 * - @Bean: Marks a method inside @Configuration class to define and return a Spring bean.
 * - @Setter: Generates setter methods for all non-final fields (PUBLIC by default).
 * - @EnableAsync: Activates Spring's annotation-driven async mechanism (e.g. @Async).
 * - @Value: Injects a single property from application.properties or application.yml.
 * - @ConfigurationProperties:
 *   + Binds a group of related properties (with a common prefix) into a class.
 *   + It also offers useful features like type safety, validation (@Validated + JSR-303), and IDE auto-completion (async.executor.core-pool-size = private int corePoolSize).
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "async.executor")
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private String threadNamePrefix;
    private int keepAliveSeconds;
    private boolean allowCoreThreadTimeout;
    private int awaitTerminationSeconds;

    /**
     * - Creates a thread pool executor for handling @Async methods asynchronously on separate threads.
     * - Bean name "taskExecutor" allows @Async to use this executor implicitly.
     *   If you create a bean with a different name (e.g., "myExecutor"), then you must use @Async("myExecutor").
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
     * ==================================================
     * Method explanation:
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
