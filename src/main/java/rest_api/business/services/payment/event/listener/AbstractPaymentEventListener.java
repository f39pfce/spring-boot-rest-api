package rest_api.business.services.payment.event.listener;

import rest_api.business.services.payment.gateway.PaymentGatewayResolver;

public class AbstractPaymentEventListener {

    //TODO create and add the logger here

    /**
     * CreditCardPayment boarding resolver
     */
    protected PaymentGatewayResolver paymentGatewayResolver;

    /**
     * Constructor
     *
     * @param paymentGatewayResolver CreditCardPayment boarding resolver
     */
    public AbstractPaymentEventListener(PaymentGatewayResolver paymentGatewayResolver) {
        this.paymentGatewayResolver = paymentGatewayResolver;
    }
}
