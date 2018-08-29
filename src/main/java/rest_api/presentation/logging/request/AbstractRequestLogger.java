package rest_api.presentation.logging.request;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Request logger base class that provides standardized formatting of request headers
 */
public abstract class AbstractRequestLogger {

    /**
     * Log the request
     *
     * @param request incoming request
     * @throws IOException on read/write failure
     */
    abstract void logRequest(HttpServletRequest request) throws IOException;

    /**
     * Format the headers in a standardized fashion
     *
     * @param request incoming request
     * @return String
     */
    String getFormattedHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder("{");
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String nextHeader = headerName + ":" + request.getHeader(headerName);
                if (headerNames.hasMoreElements()) {
                    nextHeader += ", ";
                }
                headers.append(nextHeader);
            }
        }
        headers.append(("}"));
        return headers.toString();
    }
}
