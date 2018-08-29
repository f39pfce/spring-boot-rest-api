package rest_api.business.dtos.payment;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import rest_api.business.entities.payment.CreditCardPayment;
import rest_api.business.entities.payment.PaymentType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

@Getter
public class CreditCardPaymentDto extends AbstractPaymentDto {

    /**
     * CreditCardPayment details
     */
    @NotBlank
    @XmlElement
    private String cardNumber;

    /**
     * Credit card CVV
     */
    @NotBlank
    @XmlElement
    @Size(min=3, max=3)
    private String cvv;

    /**
     * Expiration year
     */
    @NotEmpty
    @XmlElement
    @Length(min=4, max=4)
    private String expirationYear;

    /**
     * Expiration month
     */
    @NotBlank
    @XmlElement
    @Length(min=2, max=2)
    private String expirationMonth;

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
    private String cardType;

    /**
     * Constructor
     */
    public CreditCardPaymentDto() {
        super();
        paymentType = PaymentType.CREDIT_CARD;
        baseEntityClass = CreditCardPayment.class;
    }
}
