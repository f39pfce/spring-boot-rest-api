package rest_api.business.services.payment.event;

import lombok.Getter;
import rest_api.business.entities.payment.AbstractPayment;
import org.springframework.context.ApplicationEvent;

/**
 * Event that occurs when a payment is saved
 */
public class PaymentSaveEvent extends ApplicationEvent {

    /**
     * CreditCardPayment repositories
     */
    @Getter
    private AbstractPayment payment;

    /**
     * Constructor
     *
     * @param source object that fired the event
     * @param payment payment the event is about
     */
    public PaymentSaveEvent(Object source, AbstractPayment payment) {
        super(source);
        this.payment = payment;
    }
}
