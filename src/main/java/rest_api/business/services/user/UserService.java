package rest_api.business.services.user;

import org.springframework.mail.MailException;
import rest_api.business.entities.user.User;
import rest_api.presentation.controllers.user.UserExistsException;

import java.util.Optional;

/**
 * Service for interacting with User repositories
 */
public interface UserService {

    /**
     * Create a new user repositories
     *
     * @param emailAddress email address to use as user's name
     * @return String
     * @throws UserExistsException thrown if user exists
     */
    String registerNewUserAccount(String emailAddress) throws UserExistsException;

    /**
     * Send confirmation email including user's plaintext password
     *
     * @param user User repositories that was created
     * @param plaintextPassword password
     * @throws MailException thrown if there are any issues sending email
     */
    void sendConfirmationEmail(User user, String plaintextPassword) throws MailException;

    /**
     * Find a single user by both name and password
     *
     * @param userName user name
     * @param password password
     * @return Optional<User>
     */
    Optional<User> findOneByUserNameAndPassword(String userName, String password);

    /**
     * Find a single user by user name only
     *
     * @param userName user name
     * @return Optional<User>
     */
    Optional<User> findOneByUserName(String userName);

    /**
     * Generate JWT OAuth token
     *
     * @param user User repositories
     * @return String
     */
    String generateJwtToken(User user);
}
