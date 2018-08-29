package rest_api.business.services.payment.event.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import rest_api.business.entities.payment.AbstractPayment;
import rest_api.business.services.payment.event.PaymentSaveEvent;
import rest_api.business.services.payment.gateway.PaymentGatewayNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import rest_api.business.services.payment.gateway.PaymentGatewayResolver;

import java.io.IOException;

/**
 * Event listener for merchant save event
 */
@Component
public class PaymentSaveEventListener extends AbstractPaymentEventListener
        implements ApplicationListener<PaymentSaveEvent> {

    /**
     * Constructor
     *
     * @param paymentGatewayResolver CreditCardPayment boarding resolver
     */
    @Autowired
    public PaymentSaveEventListener(PaymentGatewayResolver paymentGatewayResolver) {
        super(paymentGatewayResolver);
    }

    /**
     * Logic that is run on event firing
     *
     * @param event payment delete event
     */
    public void onApplicationEvent(PaymentSaveEvent event) {
        AbstractPayment payment = event.getPayment();
        try {
            paymentGatewayResolver.getPaymentGateway(payment).board(payment);
        } catch (PaymentGatewayNotFoundException | IOException e) {
            //Log this to a failed boarding log
        }
    }
}
