package rest_api.presentation.logging.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A filter that accesses request information and logs it
 */
public class RequestLogFilter extends OncePerRequestFilter {

    /**
     * Request logger
     */
    @Autowired
    private AbstractRequestLogger requestLogger;

    /**
     * Filter the request, logging it and passing it along the chain
     *
     * @param request http request
     * @param response http response
     * @param chain filter chain
     * @throws IOException on read/write failure
     * @throws ServletException on Servlet failure
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        MultipleReadHttpRequest requestWrapper = new MultipleReadHttpRequest(request);
        requestLogger.logRequest(requestWrapper);
        chain.doFilter(requestWrapper, response);

    }
}
