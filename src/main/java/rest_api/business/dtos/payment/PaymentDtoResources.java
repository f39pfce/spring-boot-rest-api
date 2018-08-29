package rest_api.business.dtos.payment;

import org.springframework.hateoas.Resource;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of this class is to act as a wrapper to XML serialize HATEOAS CreditCardPayment Resources, all objects
 * to be marshalled by JAXB need an XML element tag, which ArrayList does not have and therefor needs to be wrapped.
 */
@XmlRootElement(name = "payments")
public class PaymentDtoResources extends ArrayList<Resource<AbstractPaymentDto>> {

    /**
     * Constructor
     *
     * No-arg constructor required for XML serialization
     */
    public PaymentDtoResources() {}

    /**
     * Constructor
     *
     * @param resources list of Resource<MerchantDto>
     */
    public PaymentDtoResources(List<Resource<AbstractPaymentDto>> resources) {
        for (Resource<AbstractPaymentDto> resource : resources) {
            this.add(resource);
        }
    }

    /**
     * Wrapper that assigns individual elements in the list the tag <payment>
     *
     * @return PaymentDtoResources
     */
    @XmlElement(name = "payment")
    public List<Resource<AbstractPaymentDto>> getMerchantDtoResources() {
        return this;
    }
}
