package rest_api.presentation.controllers.merchant;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import rest_api.Application;
import rest_api.business.entities.merchant.Merchant;
import rest_api.data_provider.MerchantProvider;
import rest_api.business.entities.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Transactional
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = Application.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MerchantIntegrationTest {

    private static String BASE_PATH = "http://localhost/v1/merchant";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EntityManager entityManager;

    private User activeUser;

    @Before
    public void setUp() {
        //Do not change name "anonymousUser", this is user used by Spring Boot
        User user = new User("anonymousUser", "password");
        user.setId(1L);
        entityManager.merge(user);
        activeUser = user;
    }

    @Test
    public void givenNoMerchantsExist_whenFindAll_thenReturnEmptyJsonArray() throws Exception {
        //given that
        //No merchant exists

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
    }

    @Test
    public void givenMerchantsExist_whenFindAll_thenReturnJsonArray() throws Exception {
        //given that
        setupMerchants();

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "[{\"id\":1,\"lastName\":\"Solo\",\"firstName\":\"Han\",\"address\":\"Hoth\",\""
                        + "city\":\"Snow Rubble City\",\"state\":\"Of Panic\",\""
                        +"email\":\"rebelscum4eva@fightthepower.com\",\"website\":\"www.resist.com\","
                        + "\"links\":[{\"rel\":\"self\",\"href\":\"" + BASE_PATH + "/1\"},"
                        + "{\"rel\":\"allPayments\",\"href\":\"" + BASE_PATH + "/1/payment\"}]},"

                + "{\"id\":2,\"lastName\":\"C3\",\"firstName\":\"P0\",\"address\":\"Tatooine\",\""
                        + "city\":\"Sand Dunes\",\"state\":\"Annoyed\",\""
                        + "email\":\"primandproper@robotemailcorp.com\",\"website\":\"www.resistance.com\","
                        + "\"links\":[{\"rel\":\"self\",\"href\":\"" + BASE_PATH + "/2\"},"
                        + "{\"rel\":\"allPayments\",\"href\":\"" + BASE_PATH + "/2/payment\"}]},"

                + "{\"id\":3,\"lastName\":\"R2\",\"firstName\":\"D2\",\"address\":\"Tatooine\",\""
                        + "city\":\"Scrap Yard\",\"state\":\"Always pleasant\",\""
                        + "email\":\"beepboopbeep@beepboopbeep.com\",\"website\":\"www.beepboopbeep.com\","
                        + "\"links\":[{\"rel\":\"self\",\"href\":\"" + BASE_PATH + "/3\"},"
                        + "{\"rel\":\"allPayments\",\"href\":\"" + BASE_PATH + "/3/payment\"}]}]"
        );
    }

    @Test
    public void givenMerchantsExist_whenFindAllWithPagenation_thenReturnJsonArray() throws Exception {
        //given that
        setupMerchants();

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant?page=1&size=2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "[{\"id\":3,\"lastName\":\"R2\",\"firstName\":\"D2\",\"address\":\"Tatooine\",\""
                        + "city\":\"Scrap Yard\",\"state\":\"Always pleasant\",\""
                        + "email\":\"beepboopbeep@beepboopbeep.com\",\"website\":\"www.beepboopbeep.com\","
                        + "\"links\":[{\"rel\":\"self\",\"href\":\"" + BASE_PATH + "/3\"},"
                        + "{\"rel\":\"allPayments\",\"href\":\"" + BASE_PATH + "/3/payment\"}]}]"
        );
    }

    @Test
    public void givenMerchantIsSaved_thenReturnConfirmationText() throws Exception{
        //given that
        //merchant being saved

        //then perform
        MvcResult result = mvc.perform(post("/v1/merchant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\" : \"Han\", \"lastName\" : \"Solo\"," +
                        "\"address\" : \"Hoth\", \"city\" : \"Snow Rubble City\"," +
                        "\"state\" : \"Of Panic\", \"email\" : \"rebelscum4eva@fightthepower.com\"," +
                        "\"website\" : \"www.resist.com\"}"
                )
        ).andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "{\"id\":1,\"lastName\":\"Solo\",\"firstName\":\"Han\",\"address\":\"Hoth\",\""
                        + "city\":\"Snow Rubble City\",\"state\":\"Of Panic\",\""
                        +"email\":\"rebelscum4eva@fightthepower.com\",\"website\":\"www.resist.com\","
                        + "\"_links\":{\"self\":{\"href\":\"" + BASE_PATH + "/1\"},"
                        + "\"allPayments\":{\"href\":\"" + BASE_PATH + "/1/payment\"}}}"
        );
    }

    @Test
    public void givenMerchantMissingFieldsIsSaved_thenReturnBadRequest() throws Exception{
        //given that
        //merchant being saved

        //then perform
        mvc.perform(post("/v1/merchant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"lastName\" : \"Solo\"," +
                        "\"address\" : \"Hoth\", \"city\" : \"Snow Rubble City\"," +
                        "\"state\" : \"Of Panic\", \"email\" : \"rebelscum4eva@fightthepower.com\"," +
                        "\"website\" : \"www.resist.com\"}"
                )
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void givenMerchantIdIsFound_thenDeleteMerchantSucceeds() throws Exception {
        //given that
        setupMerchant();

        //then perform
        MvcResult result = mvc.perform(delete("/v1/merchant/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
    }

    @Test
    public void givenMerchantIdIsNotFound_thenDeleteMerchantFails() throws Exception {
        //given that
        //No merchant exists

        //then perform
        mvc.perform(delete("/v1/merchant/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void givenNoMerchantsExist_whenFindOne_thenReturnEmptyJsonArray() throws Exception {
        //given that
        //No merchant exists

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo("");
    }

    @Test
    public void givenMerchantWithIdExists_whenFindOne_thenReturnMerchant() throws Exception {
        //given that
        setupMerchant();

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "{\"id\":1,\"lastName\":\"Solo\",\"firstName\":\"Han\",\"address\":\"Hoth\",\""
                        + "city\":\"Snow Rubble City\",\"state\":\"Of Panic\",\""
                        +"email\":\"rebelscum4eva@fightthepower.com\",\"website\":\"www.resist.com\","
                        + "\"_links\":{\"self\":{\"href\":\"" + BASE_PATH + "/1\"},"
                        + "\"allPayments\":{\"href\":\"" + BASE_PATH + "/1/payment\"}}}"
        );
    }

    @Test
    public void givenNoMerchantsExist_whenUpdateOnMerchant_thenReturnNotFound() throws Exception {
        //given that
        //No merchant exists

        //then perform
        mvc.perform(put("/v1/merchant/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\" : \"Han\", \"lastName\" : \"Solo\"," +
                        "\"address\" : \"Hoth\", \"city\" : \"Snow Rubble City\"," +
                        "\"state\" : \"Of Panic\", \"email\" : \"rebelscum4eva@fightthepower.com\"," +
                        "\"website\" : \"www.resist.com\"}"
                )
        ).andExpect(status().isNotFound());
    }

    @Test
    public void givenMerchantsExist_whenUpdateOnMerchant_thenReturnSuccess() throws Exception {
        //given that
        setupMerchant();

        //then perform
        MvcResult result = mvc.perform(put("/v1/merchant/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\" : \"Han\", \"lastName\" : \"Solo\"," +
                        "\"address\" : \"Hoth\", \"city\" : \"Snow Rubble City\"," +
                        "\"state\" : \"Of Panic\", \"email\" : \"rebelscum4eva@fightthepower.com\"," +
                        "\"website\" : \"www.resist.com\"}"
                )
        ).andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "{\"id\":1,\"lastName\":\"Solo\",\"firstName\":\"Han\",\"address\":\"Hoth\",\""
                        + "city\":\"Snow Rubble City\",\"state\":\"Of Panic\",\""
                        +"email\":\"rebelscum4eva@fightthepower.com\",\"website\":\"www.resist.com\","
                        + "\"_links\":{\"self\":{\"href\":\"" + BASE_PATH + "/1\"},"
                        + "\"allPayments\":{\"href\":\"" + BASE_PATH + "/1/payment\"}}}"
        );
    }

    @Test
    public void givenMissingFieldsInUpdate_whenUpdateAttempted_returnBadRequest() throws Exception {
        //given that
        setupMerchant();

        //then perform
        mvc.perform(put("/v1/merchant/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"lastName\" : \"Solo\"," +
                        "\"address\" : \"Hoth\", \"city\" : \"Snow Rubble City\"," +
                        "\"state\" : \"Of Panic\", \"email\" : \"rebelscum4eva@fightthepower.com\"," +
                        "\"website\" : \"www.resist.com\"}"
                )
        ).andExpect(status().isBadRequest());
    }

    private void setupMerchant() {
        Merchant merchant = MerchantProvider.getMerchant();
        merchant.setOwner(activeUser);
        entityManager.merge(merchant);
    }

    private void setupMerchants() {
        for (Merchant merchant : MerchantProvider.getMerchants()) {
            merchant.setOwner(activeUser);
            entityManager.merge(merchant);
        }
    }
}
