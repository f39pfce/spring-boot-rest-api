package rest_api.presentation.controllers.user;

import org.glassfish.hk2.runlevel.RunLevelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rest_api.business.dtos.user.UserDto;
import rest_api.business.entities.user.User;
import rest_api.business.services.user.UserService;
import rest_api.presentation.security.UnauthorizedAccessException;
import rest_api.presentation.security.oauth.JwtToken;
import javax.validation.Valid;
import java.util.Optional;

/**
 * Controller for actions regarding API users such as creation or joken acquisition
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * Service for interactions with User repositories
     */
    private UserService userService;

    /**
     * Constructor
     *
     * @param userService business for interactions with User repositories
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create a new user
     *
     * @param userDto client representation of a user repositories
     * @return UserDto
     * @throws UserExistsException thrown if user already exists
     */
    @PostMapping("/create")
    public UserDto createNewUser(@Valid @RequestBody UserDto userDto) throws UserExistsException {
        String plainTextPassword = userService.registerNewUserAccount(userDto.getUserName());
        Optional<User> optionalUser = userService.findOneByUserName(userDto.getUserName());
        User user = optionalUser.orElseThrow(RunLevelException::new);
        userService.sendConfirmationEmail(user, plainTextPassword);
        return new UserDto(user.getUserName(), plainTextPassword);
    }

    /**
     * Generate OAuth token
     *
     * @param user user name
     * @param secretKey plaintext secret key
     * @return JwtToken
     * @throws UnauthorizedAccessException thrown if no validated user is found
     */
    @GetMapping("/token")
    public JwtToken getJwtToken(
            @RequestHeader("user") String user,
            @RequestHeader("secret_key") String secretKey) throws UnauthorizedAccessException {

        Optional<User> validatedUser = userService.findOneByUserNameAndPassword(user, secretKey);
        return new JwtToken(userService.generateJwtToken(validatedUser.orElseThrow(UnauthorizedAccessException::new)));
    }

}