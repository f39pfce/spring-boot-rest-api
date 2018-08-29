package rest_api.business.dtos.payment;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.*;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;
import rest_api.business.entities.payment.PaymentType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * CreditCardPayment DTO allowing for separation of internal representation of the payment from the version the client
 * provides/receives.
 */
@Getter
@XmlRootElement(name = "payment")
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreditCardPaymentDto.class, name = "credit_card")
})
abstract public class AbstractPaymentDto {

    /**
     * Base repositories class
     */
    protected Class baseEntityClass;

    /**
     * Payment type
     */
    protected PaymentType paymentType;

    /**
     * Merchant's ID
     */
    @XmlElement
    protected long id;

    /**
     * Amount of the payment
     */
    @NotEmpty
    @XmlElement
    protected double amount;

    /**
     * Return the type of payment
     *
     * @return PaymentType payment type
     */
    final public PaymentType getType() {
        return paymentType;
    }

    /**
     * Gets class of repositories DTO is related to
     *
     * @return Class class of base repositories
     */
    final public Class getEntityClass() {
        return baseEntityClass;
    }
}
