package rest_api.presentation.security.basic;

import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter for basic authorization
 */
public class BasicAuthorizationFilter extends OncePerRequestFilter {

    //TODO implement this
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        //compute basic auth here
        chain.doFilter(request, response);
    }
}
