package rest_api.config;

import rest_api.presentation.logging.request.RequestLogFilter;
import rest_api.presentation.security.UnauthorizedAccessHandler;
import rest_api.presentation.security.SecurityFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.naming.ConfigurationException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Handler that provides response to requests that fail authorization
     */
    private UnauthorizedAccessHandler unauthorizedHandler;

    /**
     * Factory that provides the security filter based on config values
     */
    private SecurityFactory securityFactory;

    /**
     * Translates config files to class based properties
     */
    private PropertiesConfig propertiesConfig;

    /**
     * Constructor
     *
     * @param unauthorizedHandler handler to respond to failed authorizations
     * @param securityFactory provides the security filter
     * @param propertiesConfig provides the config properties
     */
    @Autowired
    public WebSecurityConfig(
            UnauthorizedAccessHandler unauthorizedHandler,
            SecurityFactory securityFactory,
            PropertiesConfig propertiesConfig) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.securityFactory     = securityFactory;
        this.propertiesConfig    = propertiesConfig;
    }

    /**
     * Security filter that extends OncePerRequestFilter, the type used is based on config value, see SecurityFactory
     * object as to how this is resolved.
     *
     * @return OncePerRequestFilter the security filter chosen based on the config value
     * @throws ConfigurationException thrown if the config value does not match an existing security filter
     */
    @Bean
    public OncePerRequestFilter securityFilterBean() throws ConfigurationException {
        return securityFactory.securityFilter(propertiesConfig.getSecurity().getType());
    }

    /**
     * Logger that records all incoming requests
     *
     * @return RequestLogFilter
     */
    @Bean
    public RequestLogFilter requestLogFilterBean()  {
        return new RequestLogFilter();
    }

    /**
     * Main controllers security configuration, allows for pattern matching of URLs to enable/disable authentication in a fine
     * grained manner, as well as global authorization. You can enable/disable cors and csrf here, as well as provide
     * different exception handlers for cases where authorization fails.
     *
     * @param http the Spring security configuration
     * @throws Exception various components of this throw exceptions that will crash the boot process
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/user/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .addFilterBefore(securityFilterBean(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(requestLogFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * Bcrypt password encoder
     */
    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

}
