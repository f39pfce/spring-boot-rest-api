package rest_api.config;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotNull;

/**
 * See http://www.baeldung.com/configuration-properties-in-spring-boot, this class converts properties such as those
 * in application.properties into either values, lists or objects - this allows end users to interact with config values
 * as objects or lists in cases where the config values are related to one another
 */
@Setter
@Getter
@Validated
@Configuration
@ConfigurationProperties(prefix = "application")
public class PropertiesConfig {

    /**
     * How request logs are stored, must match enum of RequestLogType
     */
    @NotBlank
    private String requestLogType;

    /**
     * If when merchants are saved boarding to First Data occurs
     */
    private boolean firstDataBoardOnSave;

    /**
     * If when payments are saved boarding to IPG occurs
     */
    private boolean ipgBoardOnSave;

    /**
     * Credentials for First Data boarding
     */
    private FirstDataBoarding firstDataBoarding = new FirstDataBoarding();

    /**
     * Credentials for Bluepay API email
     */
    private BluepayApiEmail bluepayApiEmail = new BluepayApiEmail();

    /**
     * Security settings
     */
    private Security security = new Security();

    /**
     * PayEezy credentials
     */
    private PayeezyGateway payeezyGateway = new PayeezyGateway();

    /**
     * Credentials for First Data boarding
     */
    @Setter
    @Getter
    public static class FirstDataBoarding {

        /**
         * First Data boarding URL
         */
        @URL
        @NotBlank
        private String url;

        /**
         * First Data boarding API user name
         */
        @Email
        @NotBlank
        private String user;

        /**
         * First Data boarding API secret key
         */
        @NotBlank
        private String secretKey;
    }

    /**
     * Credentials for Bluepay API email
     */
    @Setter
    @Getter
    public static class BluepayApiEmail {

        /**
         * Host name
         */
        @NotBlank
        private String host;

        /**
         * Port to connect to
         */
        @NotNull
        private Integer port;

        /**
         * Username (email address)
         */
        @NotNull
        private String username;

        /**
         * User's password
         */
        @NotNull
        private String password;
    }

    /**
     * Security settings
     */
    @Setter
    @Getter
    public static class Security {
        /**
         * Security type, must match enum of SecurityType
         */
        @NotBlank
        private String type;

        /**
         * The signing key shared between client and server
         */
        @NotBlank
        private String jwtSigningKey;

        /**
         * Number of seconds the OAuth token remains valid
         */
        @NotNull
        private Integer jwtTokenValidSeconds;
    }

    /**
     * PayEezy API credentials
     */
    @Setter
    @Getter
    public static class PayeezyGateway {
        /**
         * Boarding API
         */
        @URL
        @NotBlank
        private String url;

        /**
         * API key - currently using same key for all merchants for testing purposes
         */
        @NotBlank
        private String apiKey;

        /**
         * API secret - tied to apiKey above
         */
        @NotBlank
        private String apiSecret;

        /**
         * API token - tied to apiKey above
         */
        @NotBlank
        private String token;
    }
}
