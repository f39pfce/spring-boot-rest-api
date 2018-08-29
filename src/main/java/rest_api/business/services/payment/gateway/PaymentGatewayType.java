package rest_api.business.services.payment.gateway;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * CreditCardPayment boarding type
 */
public enum PaymentGatewayType {
    Bluepay, Payeezy;

    /**
     * How enum is represented in JSON form
     *
     * @return String
     */
    @JsonValue
    public String toString() {
        return super.toString();
    }
}
