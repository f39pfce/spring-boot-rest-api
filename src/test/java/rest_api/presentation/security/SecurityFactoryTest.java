package rest_api.presentation.security;

import static org.assertj.core.api.Assertions.*;

import rest_api.presentation.security.hmac.HmacAuthenticationFilter;
import rest_api.presentation.security.oauth.JwtAuthenticationFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.naming.ConfigurationException;

@RunWith(SpringRunner.class)
public class SecurityFactoryTest {

    @Test
    public void givenOAuthSecurity_whenGetSecurityFilter_returnJwtAuthenticationFilter()
            throws ConfigurationException {
        //given that
        String securityType = SecurityType.OAUTH.toString();

        //then perform
        OncePerRequestFilter requestFilter = new SecurityFactory().securityFilter(securityType);

        //and expect
        assertThat(requestFilter.getClass()).isEqualTo(JwtAuthenticationFilter.class);
    }

    @Test
    public void givenOAuthSecurity_whenGetSecurityFilter_returnHmacAuthenticationFilter()
            throws ConfigurationException {
        //given that
        String securityType = SecurityType.HMAC.toString();

        //then perform
        OncePerRequestFilter requestFilter = new SecurityFactory().securityFilter(securityType);

        //and expect
        assertThat(requestFilter.getClass()).isEqualTo(HmacAuthenticationFilter.class);
    }

    @Test(expected = ConfigurationException.class)
    public void givenInvalidSecurityType_whenGetSecurityFilter_thenThrowConfigurationException()
            throws ConfigurationException {
        //given that
        String securityType = "random";

        //then perform
        OncePerRequestFilter requestFilter = new SecurityFactory().securityFilter(securityType);
        assertThat(requestFilter).isNull();
    }
}
