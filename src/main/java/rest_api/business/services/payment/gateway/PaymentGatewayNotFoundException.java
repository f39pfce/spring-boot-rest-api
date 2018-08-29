package rest_api.business.services.payment.gateway;

/**
 * Thrown when a boarding is expected to be found but is not
 */
public class PaymentGatewayNotFoundException extends Exception {

    /**
     * Constructor
     */
    public PaymentGatewayNotFoundException() {}

    /**
     * Constructor
     *
     * @param message message to pass to parent exception
     */
    public PaymentGatewayNotFoundException(String message) {
        super(message);
    }
}