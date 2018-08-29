package rest_api.business.services.payment.gateway;

import rest_api.business.entities.payment.AbstractPayment;

import java.io.IOException;

/**
 * CreditCardPayment boarding for boarding credit card and ACH transactions
 */
public interface PaymentGateway {

    /**
     * Board payment to the boarding
     *
     * @param payment CreditCardPayment
     */
    void board(AbstractPayment payment) throws IOException;

    /**
     * Return the payment boarding type
     *
     * @return PaymentGatewayType
     */
    PaymentGatewayType getType();
}
