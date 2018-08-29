package rest_api.presentation.logging.request;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.naming.ConfigurationException;

/**
 * Provides the request logger based on the value stored in the config, allows configurable swapping of what logger
 * is used. This is effectively acting as a bridge to use ApplicationContext.getBean based on a config variable.
 */
@Component
public class RequestLoggerFactory implements ApplicationContextAware {

    /**
     * Application context
     */
    private ApplicationContext applicationContext;

    /**
     * Setter
     *
     * @param applicationContext application context
     * @throws BeansException on app. context failure
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Factory method to return the logger type based on the config parameter
     *
     * @param type must match a RequestLogType enum
     * @return AbstractRequestLogger
     * @throws ConfigurationException thrown when type not equal to enums listed below
     */
    public AbstractRequestLogger requestLog(String type) throws ConfigurationException {
        if (type.equals(RequestLogType.DATABASE.toString())) {
            return this.applicationContext.getBean(RequestDatabaseLogger.class);
        } else if (type.equals(RequestLogType.FILE.toString())) {
            return applicationContext.getBean(RequestFileLogger.class);
        } else {
            throw new ConfigurationException();
        }
    }
}
