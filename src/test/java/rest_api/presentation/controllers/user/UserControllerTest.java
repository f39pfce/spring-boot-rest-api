package rest_api.presentation.controllers.user;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rest_api.business.entities.user.User;
import rest_api.business.services.user.UserService;

import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class, secure = false)
@EnableSpringDataWebSupport
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test(expected = UserExistsException.class)
    public void givenUserExists_whenCreateUser_returnBadRequest() throws Exception {
        //given that
        when(userService.registerNewUserAccount(any(String.class))).thenThrow(new UserExistsException());

        //then perform
        MvcResult result = mvc.perform(post("/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userName\" : \"aherrington@bluepay.com\"}"))
                .andExpect(status().isBadRequest()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo("\"User already exists.\"");
        verify(userService.registerNewUserAccount(any(String.class)), times(1));
    }

    @Test
    public void givenUserIsValid_whenCreateUser_saveSuccessfully() throws Exception {
        //given that
        String emailAddress = "email@address.com";
        String password = "password";
        User existingUser = new User(emailAddress, password);
        when(userService.registerNewUserAccount(emailAddress)).thenReturn(password);
        when(userService.findOneByUserName(emailAddress)).thenReturn(Optional.of(existingUser));

        //then perform
        MvcResult result = mvc.perform(post("/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userName\" : \"" + emailAddress + "\"}"))
                .andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "{\"userName\":\"email@address.com\",\"secretKey\":\"" + password + "\"}"
        );
        verify(userService, times(1)).registerNewUserAccount(emailAddress);
        verify(userService, times(1)).findOneByUserName(emailAddress);
        verify(userService, times(1)).sendConfirmationEmail(existingUser, password);
    }

    @Test
    public void givenBadCredentials_whenAttemptGetJwtToken_returnNotAllowed() throws Exception {
        String emailAddress = "email@address.com";
        String password = "password";
        when(userService.findOneByUserNameAndPassword(emailAddress, password)).thenReturn(Optional.empty());

        //then perform
        mvc.perform(get("/user/token")
                .contentType(MediaType.APPLICATION_JSON)
                .header("user", emailAddress)
                .header("secret_key", password))
                .andExpect(status().isUnauthorized()).andReturn();

        //and expect
        verify(userService, times(1)).findOneByUserNameAndPassword(emailAddress, password);
    }

    @Test
    public void givenGoodCredentials_whenAttemptGetJwtToken_returnToken() throws Exception {
        String emailAddress = "email@address.com";
        String password     = "password";
        String token        = "token";
        Optional<User> user = Optional.of(new User());

        when(userService.generateJwtToken(any(User.class))).thenReturn(token);
        when(userService.findOneByUserNameAndPassword(emailAddress, password)).thenReturn(user);

        //then perform
        MvcResult result = mvc.perform(get("/user/token")
                .contentType(MediaType.APPLICATION_JSON)
                .header("user", emailAddress)
                .header("secret_key", password))
                .andExpect(status().isOk()).andReturn();

        //and expect
        verify(userService, times(1)).generateJwtToken(any(User.class));
        verify(userService, times(1)).findOneByUserNameAndPassword(emailAddress, password);
        assertThat(result.getResponse().getContentAsString()).isEqualTo("{\"value\":\"" + token + "\"}");
    }
}
