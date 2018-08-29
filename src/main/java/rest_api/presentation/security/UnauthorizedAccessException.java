package rest_api.presentation.security;

/**
 * Exception thrown when unauthorized access to the API is attempted
 */
public class UnauthorizedAccessException extends Exception {

    /**
     * Constructor
     */
    public UnauthorizedAccessException() {}

    /**
     * Constructor
     *
     * @param message passed to parent Exception
     */
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
