package rest_api.presentation.security.hmac;

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
public class HmacAuthenticationFilterTest {

    @Autowired
    private HmacUtil hmacUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EntityManager entityManager;


    private MockMvc mvc;

    private boolean setupComplete = false;

    @Before
    public void setUp() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(null);
        if (!setupComplete) {
            User user = new User("aherrington@bluepay.com", "password");
            user.setId(1L);
            entityManager.merge(user);

            HmacAuthenticationFilter hmacAuthenticationFilter = new HmacAuthenticationFilter();

            Field hmacUtilField = Class.forName("rest_api.presentation.security.hmac.HmacAuthenticationFilter")
                    .getDeclaredField("hmacUtil");
            hmacUtilField.setAccessible(true);
            hmacUtilField.set(hmacAuthenticationFilter, hmacUtil);

            Field userServiceField = Class.forName("rest_api.presentation.security.hmac.HmacAuthenticationFilter")
                    .getDeclaredField("userService");
            userServiceField.setAccessible(true);
            userServiceField.set(hmacAuthenticationFilter, userService);

            Field userDetailsServiceField = Class.forName("rest_api.presentation.security.hmac.HmacAuthenticationFilter")
                    .getDeclaredField("userDetailsService");
            userDetailsServiceField.setAccessible(true);
            userDetailsServiceField.set(hmacAuthenticationFilter, userDetailsService);

            mvc = MockMvcBuilders.webAppContextSetup(context)
                    .addFilter(hmacAuthenticationFilter, "/*")
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
                .header("Authorization", "5ZOOQBRxVHftINfZ73vkrtmaj9iSVMNycDCbKjjKJA8=")
                .header("user", "aherrington@bluepay.com")
                .header("Date", "22 02 2018 01:51:03")
                .header("Content-MD5", "3da8f17aeb10a49d798f82c7d2b97592")
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
                .header("Authorization", "BadValue")
                .header("user", "aherrington@bluepay.com")
                .header("Date", "22 02 2018 01:51:03")
                .header("Content-MD5", "3da8f17aeb10a49d798f82c7d2b97592")
                .content(body)
        ).andExpect(status().isUnauthorized());
    }
}
