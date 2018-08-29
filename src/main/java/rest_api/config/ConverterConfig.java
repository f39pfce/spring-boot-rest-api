package rest_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.Resource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import rest_api.business.dtos.merchant.MerchantDto;
import rest_api.business.dtos.merchant.MerchantDtoResources;
import rest_api.business.entities.payment.CreditCardPayment;
import rest_api.business.dtos.payment.AbstractPaymentDto;
import rest_api.business.dtos.payment.CreditCardPaymentDto;
import rest_api.business.dtos.payment.PaymentDtoResources;
import rest_api.business.dtos.user.UserDto;
import rest_api.presentation.security.oauth.JwtToken;

import java.util.List;

/**
 * Extention of the Spring WebMvcConfigurerAdapter to overwrite how messages are converted, used as part of content
 * negotiation. By default, Spring uses a JaxB2Marshaller that only sets the "setClassesToBeBound" to one object, which
 * in the case of a Resource<Merchant> results in a failure because the marshaller does not see the Merchant. This
 * config is being used here to provide multiple required classes to the XML marshaller.
 */
@Configuration
@EnableWebMvc
public class ConverterConfig extends WebMvcConfigurerAdapter {

    /**
     * Add custom converters allowing for handling of JSON, XML, etc conversion of response body
     *
     * @param converters List of custom conversions provided by config overrides
     */
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(createXmlHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter());
        super.configureMessageConverters(converters);
    }

    /**
     * Provide a custom converter that can XML serialize Resource objects
     *
     * @return MarshallingHttpMessageConverter
     */
    private HttpMessageConverter<Object> createXmlHttpMessageConverter() {
        MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        //Any classes you need to XML serialize for HATEOAS must be placed here
        //TODO refactor this to not have to add this manually, tag classes with annotation maybe?
        marshaller.setClassesToBeBound(
                MerchantDtoResources.class,
                PaymentDtoResources.class,
                AbstractPaymentDto.class,
                CreditCardPaymentDto.class,
                MerchantDto.class,
                CreditCardPayment.class,
                UserDto.class,
                JwtToken.class,
                Resource.class
        );

        xmlConverter.setMarshaller(marshaller);
        xmlConverter.setUnmarshaller(marshaller);
        return xmlConverter;
    }
}
