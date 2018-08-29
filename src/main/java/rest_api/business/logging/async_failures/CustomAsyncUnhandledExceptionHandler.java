package rest_api.business.logging.async_failures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 * Handles any unhandled event from asynchronous threads
 */
public class CustomAsyncUnhandledExceptionHandler implements AsyncUncaughtExceptionHandler {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger("exceptions-log");

    /**
     * Log asynchronous exception information
     *
     * @param ex exception
     * @param method method that threw the exception
     * @param params parameters to the method
     */
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {

        logger.error(String.format("Unexpected error occurred invoking async " +
                "method '%s'.", method), ex);
    }
}
