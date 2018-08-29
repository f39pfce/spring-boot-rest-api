package rest_api.presentation.security.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Security filter that confirms the JWT token provided is valid and still active
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Service to interact with UserDetails repositories
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * JWT token util class
     */
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Extract user name from the token, confirm the token is valid against the user's hashed security key, and save
     * the user's name in the security context is the checks are valid
     *
     * @param request http request
     * @param response http response
     * @param chain filter chain
     * @throws IOException thrown on read/write failure
     * @throws ServletException thrown on Servlet failure
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        Optional<String> header = Optional.ofNullable(request.getHeader("Authorization"));

        header.ifPresent(h -> {
            String userName  = null;
            String authToken = null;
            if (header.get().startsWith("Bearer ")) {
                authToken = header.get().replace("Bearer ","");
                userName = jwtTokenUtil.getUsernameFromToken(authToken);
            }
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails;
                try {
                    userDetails = userDetailsService.loadUserByUsername(userName);
                } catch (UsernameNotFoundException e) {
                    return;
                }
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
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
        chain.doFilter(request, response);
    }
}