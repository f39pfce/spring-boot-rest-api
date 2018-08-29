package rest_api.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import rest_api.presentation.logging.request.AbstractRequestLogger;
import rest_api.presentation.logging.request.RequestLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import javax.naming.ConfigurationException;
import java.security.SecureRandom;
import java.util.Properties;

/**
 * Main application config, used to store custom project specific bean definitions and provide annotations. JPA
 * annotations extracted from main Application.java to prevent collisions with testing frameworks.
 */
@Configuration
@ComponentScan
@EnableJpaAuditing
public class ApplicationConfig {

    /**
     * Provides a logger for incoming requests
     */
    private RequestLoggerFactory requestLoggerFactory;

    /**
     * Translates config files to class based properties
     */
    private PropertiesConfig propertiesConfig;

    /**
     * Constructor
     *
     * @param requestLoggerFactory provides a logger for incoming requests
     * @param propertiesConfig translates config files to class based properties
     */
    @Autowired
    public ApplicationConfig(RequestLoggerFactory requestLoggerFactory, PropertiesConfig propertiesConfig) {
        this.requestLoggerFactory = requestLoggerFactory;
        this.propertiesConfig     = propertiesConfig;
    }

    /**
     * Send outgoing mail from the application
     *
     * @return JavaMailSender
     */
    @Bean(name = "mailer")
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(propertiesConfig.getBluepayApiEmail().getHost());
        mailSender.setPort(propertiesConfig.getBluepayApiEmail().getPort());
        mailSender.setUsername(propertiesConfig.getBluepayApiEmail().getUsername());
        mailSender.setPassword(propertiesConfig.getBluepayApiEmail().getPassword());

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.debug", "true");
        properties.put("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.starttls.required", "true");
        properties.setProperty("mail.smtp.ssl.enable", "false");
        properties.setProperty("mail.transport.protocol", "smtp");

        return mailSender;
    }

    /**
     * The logger used for incoming requests based on the config value provided
     *
     * @return AbstractRequestLogger logger to record requests via different formats (db, file, etc)
     * @throws ConfigurationException if the config value does not match enum of RequestLogType
     */
    @Bean
    public AbstractRequestLogger requestLogger() throws ConfigurationException {
        return requestLoggerFactory.requestLog(propertiesConfig.getRequestLogType());
    }

    /**
     * This is where custom mapping rules between Dto and repositories can be defined
     *
     * @return ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return new ModelMapper();
    }

    /**
     * Secure random number generator
     *
     * @return SecureRandom
     */
    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }
}