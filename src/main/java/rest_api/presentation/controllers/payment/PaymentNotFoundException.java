package rest_api.presentation.controllers.payment;

/**
 * Exception thrown in the context of when a payment cannot be found, but should be able to be
 */
public class PaymentNotFoundException extends Exception {

    /**
     * Constructor
     */
    public PaymentNotFoundException() {}

    /**
     * Constructor
     *
     * @param message passed to parent Exception
     */
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
