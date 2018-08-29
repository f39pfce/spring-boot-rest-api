package rest_api.business.services.user;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import rest_api.Application;
import rest_api.config.PropertiesConfig;
import rest_api.business.entities.user.User;
import rest_api.persistance.repositories.user.UserRepository;
import rest_api.presentation.security.oauth.JwtTokenUtil;
import rest_api.presentation.security.SecurityType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import rest_api.presentation.controllers.user.UserExistsException;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PropertiesConfig propertiesConfig;

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserServiceImpl.UuidProvider uuidProvider;

    @Test
    public void givenUserDoesNotExist_whenFindByUserName_thenReturnEmptyOptional() {
        //given that
        String userName = "username";
        when(userRepository.findOneByUserName(userName)).thenReturn(Optional.empty());

        //then perform
        Optional<User> user = userService.findOneByUserName(userName);

        //and expect
        assertThat(user).isEqualTo(Optional.empty());
        verify(userRepository, times(1)).findOneByUserName(userName);
    }

    @Test
    public void givenValidCredentials_whenFindOneByUserNameAndPassword_thenReturnUser() {
        //given that
        String userName = "userName";
        String password = "password";

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String token            = encoder.encode(password);

        User existingUser = new User(userName, token);
        when(userRepository.findOneByUserName(userName)).thenReturn(Optional.of(existingUser));

        //then perform
        Optional<User> user = userService.findOneByUserNameAndPassword(userName, password);

        //and expect
        assertThat(user.isPresent());
        assertThat(user.get().getId()).isEqualTo(existingUser.getId());
        verify(userRepository, times(1)).findOneByUserName(userName);
    }

    @Test
    public void givenInvalidCredentials_whenFindOneByUserNameAndPassword_thenReturnEmptyOptional() {
        //given that
        String userName    = "userName";
        String password    = "password";
        String badPassword = "badPassword";
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String token = encoder.encode(password);
        User existingUser = new User(userName, token);
        when(userRepository.findOneByUserName(userName)).thenReturn(Optional.of(existingUser));

        //then perform
        Optional<User> user = userService.findOneByUserNameAndPassword(userName, badPassword);

        //and expect
        assertThat(user).isEqualTo(Optional.empty());
        verify(userRepository, times(1)).findOneByUserName(userName);
    }

    @Test
    public void givenUserExists_whenFindOneByUserName_thenReturnUser() {
        //given that
        String userName = "username";
        User userToReturn = new User(userName, "secretKey");
        when(userRepository.findOneByUserName(any(String.class))).thenReturn(Optional.of(userToReturn));

        //then perform
        Optional<User> user = userService.findOneByUserName(userName);

        //and expect
        assertThat(user.isPresent());
        assertThat(user.get().getUserName()).isEqualTo(userToReturn.getUserName());
        verify(userRepository, times(1)).findOneByUserName(userName);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void givenUserDoesNotExist_whenLoadByUserName_throwUsernameNotFoundException() {
        //given that
        String userName = "username";
        when(userRepository.findOneByUserName(any(String.class))).thenReturn(Optional.empty());

        //then perform
        userDetailsService.loadUserByUsername(userName);

        //and expect
        verify(userRepository, times(1)).findOneByUserName(userName);
    }

    @Test
    public void givenUserExists_whenLoadByUserName_returnUserDetails() {
        //given that
        String userName   = "username";
        String secretKey  = "secretKey";
        User userToReturn = new User(userName, secretKey);
        when(userRepository.findOneByUserName(any(String.class))).thenReturn(Optional.of(userToReturn));

        //then perform
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        //and expect
        verify(userRepository, times(1)).findOneByUserName(userName);
        assertThat(userDetails.getUsername()).isEqualTo(userName);
        assertThat(userDetails.getPassword()).isEqualTo(secretKey);
        assertThat(userDetails.getAuthorities().size()).isEqualTo(1);
    }

    @Test
    public void givenTokenIsRequested_whenGenerateJwtToken_returnToken() {
        //given that
        String token = "token";
        when(jwtTokenUtil.generateToken(any(User.class))).thenReturn(token);

        //then perform
        String returnedToken = userService.generateJwtToken(new User());

        //and expect
        verify(jwtTokenUtil, times(1)).generateToken(any(User.class));
        assertThat(returnedToken).isEqualTo(token);
    }

    @Test
    public void givenOAuthSecurityType_whenRegisterNewUserAccount_encrpytPassword() throws Exception {
        //given that
        String email           = "emailAddress";
        String password        = "password";
        String encodedPassword = "encodedPassword";
        propertiesConfig.getSecurity().setType(SecurityType.OAUTH.toString());
        when(uuidProvider.getUuid()).thenReturn(password);
        when(userRepository.findOneByUserName(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        //then perform
        String returnedPassword = userService.registerNewUserAccount(email);

        //and expect
        ArgumentCaptor<User> capturedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(capturedUser.capture());
        assertThat(capturedUser.getValue().getUserName()).isEqualTo(email);
        assertThat(capturedUser.getValue().getSecretKey()).isEqualTo(encodedPassword);
        assertThat(returnedPassword).isEqualTo(password);
    }

    @Test
    public void givenHmacSecurityType_whenRegisterNewUserAccount_encrpytPassword() throws Exception {
        //given that
        String email           = "emailAddress";
        String password        = "password";
        String encodedPassword = "encodedPassword";
        propertiesConfig.getSecurity().setType(SecurityType.HMAC.toString());
        when(uuidProvider.getUuid()).thenReturn(password);
        when(userRepository.findOneByUserName(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        //then perform
        String returnedPassword = userService.registerNewUserAccount(email);

        //and expect
        ArgumentCaptor<User> capturedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(capturedUser.capture());
        assertThat(capturedUser.getValue().getUserName()).isEqualTo(email);
        assertThat(capturedUser.getValue().getSecretKey()).isEqualTo(password);
        assertThat(returnedPassword).isEqualTo(password);
    }

    @Test(expected = UserExistsException.class)
    public void givenUserExists_whenRegisterNewUserAccount_throwUserExistsException() throws UserExistsException {
        //given that
        String email    = "emailAddress";
        String password = "password";
        when(userRepository.findOneByUserName(email)).thenReturn(Optional.of(new User(email, password)));
        when(uuidProvider.getUuid()).thenReturn(password);

        //then perform
        userService.registerNewUserAccount(email);

        //and expect
        //Exception to be thrown
    }


}
