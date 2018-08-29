package rest_api.presentation.security;

import rest_api.presentation.security.hmac.HmacAuthenticationFilter;
import rest_api.presentation.security.oauth.JwtAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.naming.ConfigurationException;

/**
 * Provides the security filter based on the value stored in the config, allows configurable swapping of what filter
 * is used.
 */
@Component
public class SecurityFactory {

    /**
     * Provide the security filter based on the settings in the config file
     *
     * @param securityType must match value from enum SecurityType
     * @return OncePerRequestFilter
     * @throws ConfigurationException when the security type is not enum of SecurityType
     */
    public OncePerRequestFilter securityFilter(String securityType) throws ConfigurationException {
        if (securityType.equals(SecurityType.OAUTH.toString())) {
            return new JwtAuthenticationFilter();
        } else if (securityType.equals(SecurityType.HMAC.toString())) {
            return new HmacAuthenticationFilter();
        } else {
            throw new ConfigurationException();
        }
    }
}
