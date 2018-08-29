package rest_api.business.services.user;

import org.springframework.scheduling.annotation.Async;
import rest_api.config.PropertiesConfig;
import rest_api.business.entities.user.User;
import rest_api.persistance.repositories.user.UserRepository;
import rest_api.presentation.security.oauth.JwtTokenUtil;
import rest_api.presentation.security.SecurityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import rest_api.presentation.controllers.user.UserExistsException;

import java.util.*;


@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private JavaMailSender javaMailSender;

    private UserRepository userRepository;

    private JwtTokenUtil jwtTokenUtil;

    private PasswordEncoder passwordEncoder;

    private UuidProvider uuidProvider;

    private PropertiesConfig propertiesConfig;

    @Autowired
    public UserServiceImpl(
            @Qualifier("mailer") JavaMailSender javaMailSender,
            UserRepository userRepository,
            JwtTokenUtil jwtTokenUtil,
            PasswordEncoder passwordEncoder,
            PropertiesConfig propertiesConfig,
            UserServiceImpl.UuidProvider uuidProvider
    ) {
        this.javaMailSender   = javaMailSender;
        this.userRepository   = userRepository;
        this.jwtTokenUtil     = jwtTokenUtil;
        this.passwordEncoder  = passwordEncoder;
        this.uuidProvider     = uuidProvider;
        this.propertiesConfig = propertiesConfig;
    }

    /**
     * Find a single user by both name and password
     *
     * @param userName user name
     * @param password password
     * @return Optional<User>
     */
    public Optional<User> findOneByUserNameAndPassword(String userName, String password) {
        Optional<User> userToValidate = userRepository.findOneByUserName(userName);
        if (!userToValidate.isPresent()) {
            return userToValidate;
        }
        boolean userIsValid = BCrypt.checkpw(password, userToValidate.get().getSecretKey());

        return userIsValid ? userToValidate : Optional.empty();
    }

    /**
     * Find a single user by user name only
     *
     * @param userName user name
     * @return Optional<User>
     */
    public Optional<User> findOneByUserName(String userName) {
        return userRepository.findOneByUserName(userName);
    }

    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findOneByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Username or password incorrect"));
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getSecretKey(),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    /**
     * Generate JWT OAuth token
     *
     * @param user User repositories
     * @return String
     */
    public String generateJwtToken(User user) {
        return jwtTokenUtil.generateToken(user);
    }

    /**
     * Create a new user repositories
     *
     * @param emailAddress email address to use as user's name
     * @return String
     * @throws UserExistsException thrown if user exists
     */
    public String registerNewUserAccount(String emailAddress) throws UserExistsException {
        checkIfEmailExists(emailAddress);
        String password = uuidProvider.getUuid();

        User user = new User(
                emailAddress,
                getPasswordToSave(password)
        );
        userRepository.save(user);

        return password;
    }

    /**
     * Send confirmation email including user's plaintext password
     *
     * @param user User repositories that was created
     * @param plaintextPassword password
     * @throws MailException thrown if there are any issues sending email
     */
    @Async
    public void sendConfirmationEmail(User user, String plaintextPassword) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getUserName());
        message.setSubject("Welcome to the Bluepay API");
        message.setText(
                "Your username is: " + user.getUserName() + "\n" +
                        "Your secret key is: " + plaintextPassword + "\n\n" +
                        "Please save these and keep them save!"
        );
        javaMailSender.send(message);
    }

    /**
     * Check if the email already exists
     *
     * @param emailAddress email address
     * @throws UserExistsException thrown if user already exists
     */
    private void checkIfEmailExists(String emailAddress) throws UserExistsException {
        Optional<User> user = userRepository.findOneByUserName(emailAddress);
        user.orElseThrow(UserExistsException::new);
    }

    /**
     * Logic to save either  plaintext password (HMAC) or encrpyted (everything else)
     *
     * @param secretKey secret key
     * @return String
     */
    private String getPasswordToSave(String secretKey) {
        return propertiesConfig.getSecurity().getType().equals(SecurityType.HMAC.toString()) ?
                secretKey : passwordEncoder.encode(secretKey);
    }

    /**
     * Nested class used to gain access to static randomUUID, added as class to make code testable
     */
    @Component
    public static class UuidProvider {
        public String getUuid() {
            return UUID.randomUUID().toString();
        }
    }
}
