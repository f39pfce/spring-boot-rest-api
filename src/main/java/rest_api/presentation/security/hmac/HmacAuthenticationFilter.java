package rest_api.presentation.security.hmac;

import rest_api.business.entities.user.User;
import rest_api.business.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Security filter that performs HMAC authentication
 */
public class HmacAuthenticationFilter extends OncePerRequestFilter {

    /**
     * HMAC utilities class
     */
    @Autowired
    private HmacUtil hmacUtil;

    /**
     * Service for interacting with User repositories
     */
    @Autowired
    private UserService userService;

    /**
     * Service for interacting with UserDetails repositories
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Confirm that the HMAC authentication is valid, and store the user in the security context if it is
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

        Optional<String> authHeader    = Optional.ofNullable(request.getHeader("Authorization"));
        Optional<String> dateHeader    = Optional.ofNullable(request.getHeader("Date"));
        Optional<String> contentHeader = Optional.ofNullable(request.getHeader("Content-MD5"));
        Optional<String> userHeader    = Optional.ofNullable(request.getHeader("user"));
        String method                  = request.getMethod();
        String URI                     = request.getRequestURI();

        //If any of the headers are missing or use not found, pass along without authentication causing failure
        if (authHeader.isPresent() && dateHeader.isPresent() && contentHeader.isPresent() && userHeader.isPresent()) {
            String userString = userHeader.get();
            Optional<User> optionalUser = userService.findOneByUserName(userString);
            optionalUser.ifPresent(user-> {
                String hmacToken = hmacUtil.generateHMACToken(
                        user,
                        method + URI + dateHeader.get() + userHeader.get() + contentHeader.get()
                );
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userHeader.get());

                    if (hmacToken.equals(authHeader.get())) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            });
        }
        chain.doFilter(request, response);
    }
}
