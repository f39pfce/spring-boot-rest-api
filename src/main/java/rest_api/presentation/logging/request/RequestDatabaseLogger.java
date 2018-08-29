package rest_api.presentation.logging.request;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;

/**
 * Stores request logs to database
 */
@Component
public class RequestDatabaseLogger extends AbstractRequestLogger {

    /**
     * Entity manager
     */
    private EntityManager entityManager;

    /**
     * Setter
     *
     * @param entityManager repositories manager
     */
    @Autowired
    public RequestDatabaseLogger(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Log request
     *
     * @param request incoming request
     * @throws IOException on read/write failure
     */
    @Transactional
    public void logRequest(HttpServletRequest request) throws IOException {
        RequestLog log = new RequestLog(
                request.getMethod(),
                request.getRequestURI(),
                getFormattedHeaders(request),
                new String(IOUtils.toByteArray(request.getInputStream()))
        );
        entityManager.persist(log);
    }
}
