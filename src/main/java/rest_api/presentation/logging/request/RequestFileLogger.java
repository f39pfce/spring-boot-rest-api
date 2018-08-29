package rest_api.presentation.logging.request;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Stores request logs to file
 */
public class RequestFileLogger extends AbstractRequestLogger {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger("request-log");

    /**
     * Log the request
     *
     * @param request incoming request
     * @throws IOException on read/write failure
     */
    public void logRequest(HttpServletRequest request) throws IOException {
        String body = new String(IOUtils.toByteArray(request.getInputStream()));
        logger.info(
                request.getMethod() + " | " + request.getRequestURL() +
                        "\n" + getFormattedHeaders(request) + "\n" + body
        );
    }
}
