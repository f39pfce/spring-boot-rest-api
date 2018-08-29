package rest_api.business.services.payment.event.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import rest_api.business.entities.payment.AbstractPayment;
import rest_api.business.services.payment.event.PaymentDeleteEvent;
import rest_api.business.services.payment.gateway.PaymentGatewayNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import rest_api.business.services.payment.gateway.PaymentGatewayResolver;

import java.io.IOException;

/**
 * Event listener for merchant delete event
 */
@Component
public class PaymentDeleteEventListener extends AbstractPaymentEventListener
        implements ApplicationListener<PaymentDeleteEvent> {

    /**
     * Constructor
     *
     * @param paymentGatewayResolver CreditCardPayment boarding resolver
     */
    @Autowired
    public PaymentDeleteEventListener(PaymentGatewayResolver paymentGatewayResolver) {
        super(paymentGatewayResolver);
    }

    /**
     * Logic that is run on event firing
     *
     * @param event payment delete event
     */
    public void onApplicationEvent(PaymentDeleteEvent event) {
        AbstractPayment payment = event.getPayment();
        try {
            //TODO find out of all gateways allow for delete
            paymentGatewayResolver.getPaymentGateway(payment).board(payment); //stubbed with board from the interface
        } catch (PaymentGatewayNotFoundException | IOException e) {
            //Log this to a failed boarding log
        }
    }
}
