package rest_api.business.dtos.merchant;

import org.springframework.hateoas.Resource;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of this class is to act as a wrapper to XML serialize HATEOAS Merchant Resources, all objects
 * to be marshalled by JAXB need an XML element tag, which ArrayList does not have and therefor needs to be wrapped.
 */
@XmlRootElement(name = "merchants")
public class MerchantDtoResources extends ArrayList<Resource<MerchantDto>> {

    /**
     * Constructor
     *
     * No-arg constructor required for XML serialization
     */
    public MerchantDtoResources() {}

    /**
     * Constructor
     *
     * @param resources list of Resource<MerchantDto>
     */
    public MerchantDtoResources(List<Resource<MerchantDto>> resources) {
        for (Resource<MerchantDto> resource : resources) {
            this.add(resource);
        }
    }

    /**
     * Wrapper that assigns individual elements in the list the tag <merchant>
     *
     * @return MerchantDtoResources
     */
    @XmlElement(name = "merchant")
    public List<Resource<MerchantDto>> getMerchantDtoResources() {
        return this;
    }

}
