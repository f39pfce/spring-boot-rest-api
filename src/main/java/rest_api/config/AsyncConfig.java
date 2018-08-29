package rest_api.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import rest_api.business.logging.async_failures.CustomAsyncUnhandledExceptionHandler;
import java.util.concurrent.Executor;

/**
 * Extention of the Spring AsyncConfigurer allowing for custom rule sets on the creation of the ThreadPoolExecutor
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * Return the executor used to manage application threads with custom settings
     *
     * @return Executor
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setThreadNamePrefix("RestExecutor-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    /**
     * When exceptions are uncaught in threads, provide a handler for them
     *
     * @return AsyncUncaughtExceptionHandler
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncUnhandledExceptionHandler();
    }
}
