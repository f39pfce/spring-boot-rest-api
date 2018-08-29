package rest_api.presentation.security.oauth;

import rest_api.Application;
import rest_api.business.entities.user.User;
import rest_api.business.services.user.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.reflect.Field;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = Application.class)
public class JwtAuthenticationFilterTest {

    final private String userName = "aherrington@bluepay.com";
    final private String password = "superSecretPassword";
    private String token;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private EntityManager entityManager;


    @Autowired
    private WebApplicationContext context;


    private MockMvc mvc;

    private boolean setupComplete = false;

    @Before
    public void setUp() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(null);
        if (!setupComplete) {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            User user = new User(userName, encoder.encode(password));
            user.setId(1L);
            entityManager.merge(user);
            token = userService.generateJwtToken(user);

            JwtAuthenticationFilter filter = new JwtAuthenticationFilter();

            Field userDetailsServiceField = Class.forName("rest_api.presentation.security.oauth.JwtAuthenticationFilter")
                    .getDeclaredField("userDetailsService");
            userDetailsServiceField.setAccessible(true);
            userDetailsServiceField.set(filter, userDetailsService);

            Field jwtTokenUtilField = Class.forName("rest_api.presentation.security.oauth.JwtAuthenticationFilter")
                    .getDeclaredField("jwtTokenUtil");
            jwtTokenUtilField.setAccessible(true);
            jwtTokenUtilField.set(filter, jwtTokenUtil);

            mvc = MockMvcBuilders.webAppContextSetup(context)
                    .addFilter(filter, "/*")
                    .build();

            setupComplete = true;
        }
    }

    @After
    public void tearDown() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void givenValidHeaders_whenCreateMerchant_thenSucceedInAuthentication() throws Exception {
        //given that
        //Filter is applied
        String body = "{\"firstName\" : \"Han\", \"lastName\" : \"Solo\"," +
                "\"address\" : \"Hoth\", \"city\" : \"Snow Rubble City\"," +
                "\"state\" : \"Of Panic\", \"email\" : \"rebelscum4eva@fightthepower.com\"," +
                "\"website\" : \"www.resist.com\"}";

        //then perform
        mvc.perform(post("/v1/merchant")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(body)
        ).andExpect(status().isOk());
    }

    @Test
    public void givenBadAuthorizationHash_whenCreateMerchant_thenFailAuthentication() throws Exception {
        //given that
        //Filter is applied
        String body = "{\"firstName\" : \"Han\", \"lastName\" : \"Solo\"," +
                "\"address\" : \"Hoth\", \"city\" : \"Snow Rubble City\"," +
                "\"state\" : \"Of Panic\", \"email\" : \"rebelscum4eva@fightthepower.com\"," +
                "\"website\" : \"www.resist.com\"}";

        //then perform
        mvc.perform(post("/v1/merchant")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userService.generateJwtToken(
                        new User("random", "credentials"))
                ).content(body)
        ).andExpect(status().isUnauthorized());
    }
}