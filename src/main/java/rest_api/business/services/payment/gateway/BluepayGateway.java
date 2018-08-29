package rest_api.business.services.payment.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rest_api.business.entities.payment.AbstractPayment;

/**
 * Bluepay payment boarding
 */
@Component
public class BluepayGateway extends AbstractPaymentGateway {

    /**
     * Type of the payment gateway
     */
    final private PaymentGatewayType paymentGatewayType = PaymentGatewayType.Bluepay;

    /**
     * Constructor
     *
     * @param paymentGatewayCreditCardMapper payment gateway credit card mapper
     */
    @Autowired
    public BluepayGateway(PaymentGatewayCreditCardMapper paymentGatewayCreditCardMapper) {
        super(paymentGatewayCreditCardMapper);
    }

    /**
     * Board payment to the boarding
     *
     * @param payment CreditCardPayment
     */
    public void board(AbstractPayment payment) {

    }

    /**
     * Return the payment boarding type
     *
     * @return PaymentGatewayType
     */
    public PaymentGatewayType getType() {
        return this.paymentGatewayType;
    }
}
