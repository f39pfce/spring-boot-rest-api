package rest_api.business.services.payment.gateway;

public class CreditCardMappingException extends Exception {

    public CreditCardMappingException() {}

    public CreditCardMappingException(String message) {
        super(message);
    }
}
