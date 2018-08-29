package rest_api.presentation.controllers.user;
/**
 * Exception thrown when a user attempts to create an API user where the username already exists
 */
public class UserExistsException extends Exception {

    /**
     * Constructor
     */
    public UserExistsException(){}

    /**
     * Constructor
     *
     * @param message passed to parent Exception
     */
    public UserExistsException(String message) {
        super(message);
    }
}
