package rest_api.business.entities.merchant;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import rest_api.business.entities.payment.AbstractPayment;
import rest_api.business.entities.base.AbstractUserOwnedEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;
import rest_api.business.services.payment.gateway.PaymentGatewayType;

/**
 * Entity that represents a merchant account
 */
@Entity
@Getter
@XmlRootElement
@Table(name = "merchants")
@XmlAccessorType(XmlAccessType.FIELD)
@Audited(withModifiedFlag = true)
@JsonIgnoreProperties({"owner", "payments","createdAt", "updatedAt"})
public class Merchant extends AbstractUserOwnedEntity implements Serializable {

    /**
     * List of payments owned by the merchant
     */
    @XmlTransient
    @OneToMany(mappedBy = "merchant", cascade= CascadeType.MERGE, fetch = FetchType.LAZY)
    private List<AbstractPayment> payments;

    /**
     * Merchant's last name
     */
    @Setter
    @Column(name = "last_name")
    @NotBlank
    private String lastName;

    /**
     * Merchant's first name
     */
    @Setter
    @Column(name = "first_name")
    @NotBlank
    private String firstName;

    /**
     * Merchant's street address
     */
    @Setter
    @NotBlank
    private String address;

    /**
     * Merchant's city
     */
    @Setter
    @NotBlank
    private String city;

    /**
     * Merchant's state
     */
    @Setter
    @NotBlank
    private String state;

    /**
     * Merchant's email
     */
    @Setter
    @NotBlank
    private String email;

    /**
     * Merchant's website
     */
    @Setter
    private String website;

    /**
     * PaymentGateway payments are submitted to
     */
    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentGatewayType gatewayType;
}
