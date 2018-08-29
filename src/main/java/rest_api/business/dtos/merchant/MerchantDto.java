package rest_api.business.dtos.merchant;

import lombok.Getter;
import rest_api.business.entities.merchant.Merchant;
import rest_api.business.services.payment.gateway.PaymentGatewayType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Merchant DTO allowing for separation of internal representation of the merchant from the version the client
 * provides/receives.
 */
@Getter
@XmlRootElement(name = "merchant")
public class MerchantDto {

    /**
     * Constructor
     *
     * Empty constructor required for XML serialization
     */
    public MerchantDto() {}

    /**
     * Constructor
     *
     * @param merchant accepts Merchant object and copies its relevant fields
     */
    public MerchantDto(Merchant merchant) {
        this.id          = merchant.getId();
        this.lastName    = merchant.getLastName();
        this.firstName   = merchant.getFirstName();
        this.address     = merchant.getAddress();
        this.city        = merchant.getCity();
        this.state       = merchant.getState();
        this.email       = merchant.getEmail();
        this.website     = merchant.getWebsite();
        this.gatewayType = merchant.getGatewayType();
    }

    /**
     * Merchant's ID
     */
    @XmlElement
    private Long id;

    /**
     * Merchant's last name
     */
    @XmlElement
    private String lastName;

    /**
     * Merchant's first name
     */
    @XmlElement
    private String firstName;

    /**
     * Merchant's address
     */
    @XmlElement
    private String address;

    /**
     * Merchant's city
     */
    @XmlElement
    private String city;

    /**
     * Merchant's state
     */
    @XmlElement
    private String state;

    /**
     * Merchant's email
     */
    @XmlElement
    private String email;

    /**
     * Merchant's website
     */
    @XmlElement
    private String website;

    /**
     * Merchant's boarding
     */
    @XmlElement
    private PaymentGatewayType gatewayType;
}
