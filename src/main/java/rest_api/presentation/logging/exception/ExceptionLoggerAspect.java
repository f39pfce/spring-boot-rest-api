package rest_api.presentation.logging.exception;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect that logs exceptions
 */
@Aspect
@Component
public class ExceptionLoggerAspect {

    /**
     * The exceptions logger
     */
    private static final Logger logger = LoggerFactory.getLogger("exceptions-log");

    /**
     * Pointcut for all repositories controller actions that have thrown an exception
     *
     * @param ex Exception thrown
     */
    @AfterThrowing(pointcut="execution(* rest_api.presentation.controllers.*.*Controller.*(..))", throwing="ex")
    public void controllerExceptionAdvice(Exception ex) {
        logger.error(ex.toString());
    }
}
