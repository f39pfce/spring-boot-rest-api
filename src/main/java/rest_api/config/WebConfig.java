package rest_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Override to the WebMvcConfigurerAdapter allowing for custom rules as to how content negotiation is performed
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    /**
     * Determine if content negotiation is done via Accept header, path extention ({URL}.json vs {URL}.xml)
     * or parameter ({URL}?type=json), as well as providing default type to be provided.
     *
     * @param configurer the content negotiation Spring config
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorPathExtension(false)
                .favorParameter(false)
                .ignoreAcceptHeader(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("json", MediaType.APPLICATION_JSON);
    }
}
