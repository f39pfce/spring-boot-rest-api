package rest_api.presentation.controllers.merchant;

/**
 * Exception thrown in the context of when a merchant cannot be found, but should be able to be
 */
public class MerchantNotFoundException extends Exception {

    /**
     * Constructor
     */
    public MerchantNotFoundException() {}

    /**
     * Constructor
     *
     * @param message passed to parent Exception
     */
    public MerchantNotFoundException(String message) {
        super(message);
    }
}
