package rest_api.business.services.payment.gateway;

import rest_api.business.entities.payment.AbstractPayment;

import java.io.IOException;

abstract public class AbstractPaymentGateway implements PaymentGateway {

    protected PaymentGatewayCreditCardMapper paymentGatewayCreditCardMapper;

    AbstractPaymentGateway(PaymentGatewayCreditCardMapper paymentGatewayCreditCardMapper) {
        this.paymentGatewayCreditCardMapper = paymentGatewayCreditCardMapper;
    }

    /**
     * Board payment to the boarding
     *
     * @param payment CreditCardPayment
     */
    public abstract void board(AbstractPayment payment) throws IOException;

    /**
     * Return the payment boarding type
     *
     * @return PaymentGatewayType
     */
    public abstract PaymentGatewayType getType();
}
