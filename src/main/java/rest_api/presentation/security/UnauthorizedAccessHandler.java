package rest_api.presentation.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * Handles unauthorized access detected in the config that extends WebSecurityConfigurerAdapter
 */
@Component
public class UnauthorizedAccessHandler implements AuthenticationEntryPoint, Serializable {

    /**
     * Respond with unauthorized
     *
     * @param request http request
     * @param response http response
     * @param authException authentication exception
     * @throws IOException on read/write faiulure
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}