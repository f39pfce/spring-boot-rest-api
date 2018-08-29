package rest_api.business.entities.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import rest_api.business.dtos.payment.CreditCardPaymentDto;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * CreditCardPayment detail information stored in the embedded database field of the CreditCardPayment repositories
 */
@Audited
@Getter
@XmlRootElement
@Entity(name = "credit_card")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({"merchant", "owner","createdAt", "updatedAt", "entityDtoClass", "paymentType"})
public class CreditCardPayment extends AbstractPayment {

    //TODO encrypt this
    /**
     * Credit card number
     */
    @Setter
    @NotBlank
    @XmlElement
    private String cardNumber;

    /**
     * Cardholder name
     */
    @Setter
    @NotBlank
    @XmlElement
    private String cardholderName;

    /**
     * Card type
     */
    @Setter
    @NotNull
    @XmlElement
    private CreditCardType cardType;

    /**
     * Credit card CVV
     */
    @Setter
    @NotBlank
    @XmlElement
    @Size(min=3, max=3)
    private String cvv;

    /**
     * Credit card expiration year
     */
    @Setter
    @NotEmpty
    @XmlElement
    @Length(min=4, max=4)
    private String expirationYear;

    /**
     * Credit card expiration month
     */
    @Setter
    @NotBlank
    @XmlElement
    @Length(min=2, max=2)
    private String expirationMonth;

    public CreditCardPayment() {
        super();
        this.paymentType    = PaymentType.CREDIT_CARD;
        this.entityDtoClass = CreditCardPaymentDto.class;
    }
}
