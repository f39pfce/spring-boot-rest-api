package rest_api.presentation.controllers.payment;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import rest_api.Application;
import rest_api.business.entities.payment.AbstractPayment;
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
public class PaymentIntegrationTest {

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
    public void givenNoMerchantExists_whenFindAllPaymentsByMerchantId_thenReturnNotFound() throws Exception {
        //given that
        //No merchant

        //then perform
        mvc.perform(get("/v1/merchant/1/payment")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void givenMerchantExists_whenFindAllPaymentsByMerchantId_thenReturnPayments() throws Exception {
        //given that
        createMerchant(true);

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant/1/payment")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "[{\"id\":1,\"amount\":1.23,\"cardNumber\":\"4111111111111111\","
                        + "\"cvv\":\"123\",\"expirationYear\":2017,\"expirationMonth\":\"01\","
                        + "\"links\":[{\"rel\":\"self\",\"href\":\"" + BASE_PATH + "/1/payment/1\"},"
                        + "{\"rel\":\"merchant_owner\",\"href\":\"" + BASE_PATH + "/1\"}]},"

                + "{\"id\":2,\"amount\":4.56,\"cardNumber\":\"5555555555555555\","
                        + "\"cvv\":\"456\",\"expirationYear\":2001,\"expirationMonth\":\"06\","
                        + "\"links\":[{\"rel\":\"self\",\"href\":\"" + BASE_PATH + "/1/payment/2\"},"
                        + "{\"rel\":\"merchant_owner\",\"href\":\"" + BASE_PATH + "/1\"}]},"

                + "{\"id\":3,\"amount\":7.89,\"cardNumber\":\"1234123412341234\","
                        + "\"cvv\":\"789\",\"expirationYear\":2004,\"expirationMonth\":\"03\","
                        + "\"links\":[{\"rel\":\"self\",\"href\":\"" + BASE_PATH + "/1/payment/3\"},"
                        + "{\"rel\":\"merchant_owner\",\"href\":\"" + BASE_PATH + "/1\"}]}]"
        );
    }

    @Test
    public void givenMerchantExists_whenFindAllPaymentsByMerchantIdAndPagenation_thenReturnPayments() throws Exception {
        //given that
        createMerchant(true);
        long merchantId = 1L;

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant/"+ merchantId + "/payment?page=1&size=2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "[{\"id\":3,\"amount\":7.89,\"cardNumber\":\"1234123412341234\","
                        + "\"cvv\":\"789\",\"expirationYear\":2004,\"expirationMonth\":\"03\","
                        + "\"links\":[{\"rel\":\"self\",\"href\":\"" + BASE_PATH + "/" + merchantId + "/payment/3"
                        + "\"},{\"rel\":\"merchant_owner\",\"href\":\"" + BASE_PATH + "/" + merchantId + "\"}]}]"
        );
    }

    @Test
    public void givenMerchantDoesNotExist_whenFindByMerchantAndPaymentIds_thenReturnNotFound() throws Exception {
        //given that
        //merchant does not exist

        //then perform
        mvc.perform(get("/v1/merchant/1/payment/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());;
    }

    @Test
    public void givenPaymentDoesNotExist_whenFindByMerchantAndPaymentIds_thenReturnNotFound() throws Exception {
        //given that
        createMerchant(false);

        //then perform
        mvc.perform(get("/v1/merchant/1/payment/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenPaymentAndMerchantExist_whenFindByMerchantAndPaymentIds_thenReturnPayment() throws Exception {
        //given that
        long merchantId = 1L;
        long paymentId = 1L;
        createMerchant(true);

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant/" + merchantId + "/payment/" + paymentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "{\"id\":1,\"amount\":1.23,\"cardNumber\":\"4111111111111111\","
                        + "\"cvv\":\"123\",\"expirationYear\":2017,\"expirationMonth\":\"01\","
                        + "\"_links\":{\"self\":{\"href\":\"" + BASE_PATH + "/" + merchantId + "/payment/" + paymentId
                        + "\"},\"merchant_owner\":{\"href\":\"" + BASE_PATH + "/" + merchantId + "\"}}}"
        );
    }

    @Test
    public void givenPaymentDoesNotExist_whenDeletePayment_thenThrowPaymentNotFoundException() throws Exception {
        //given that
        createMerchant(false);

        //then perform
        mvc.perform(delete("/v1/merchant/1/payment/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void givenMerchantDoesNotExist_whenDeletePayment_thenReturnNotFound() throws Exception {
        //given that
        //merchant does not exist

        //then perform
        mvc.perform(delete("/v1/merchant/1/payment/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void givenMerchantAndPaymentExist_whenDeletePayment_succeedInDeletingPayment() throws Exception {
        //given that
        createMerchant(true);

        //then perform
         mvc.perform(delete("/v1/merchant/1/payment/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenMerchantDoesNotExist_whenCreatePayment_throwMerchantNotFoundException() throws Exception {
        //given that
        //merchant does not exist

        //then perform
        mvc.perform(post("/v1/merchant/1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 5.5,\n" +
                        "    \"creditCard\": {\n" +
                        "        \"cardNumber\": \"411111111111111\",\n" +
                        "        \"cvv\": \"116\",\n" +
                        "        \"expirationDate\": \"2018-02-16\"\n" +
                        "    }\n" +
                        "}"
                )
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void givenMerchantExists_whenCreatePayment_thenSaveSuccessfully() throws Exception {
        //given that
        long merchantId = 1L;
        long paymentId = 1L;
        createMerchant(false);

        //then perform
        MvcResult result = mvc.perform(post("/v1/merchant/"+ merchantId + "/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"amount\":5.50, \"cardNumber\":4111111111111111,"
                                + "\"cvv\":116,\"expirationMonth\":\"01\",\"expirationYear\":2017}"
                )
        ).andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "{\"id\":1,\"amount\":5.5,\"cardNumber\":\"4111111111111111\","
                        + "\"cvv\":\"116\",\"expirationYear\":2017,\"expirationMonth\":\"01\","
                        + "\"_links\":{\"self\":{\"href\":\"" + BASE_PATH + "/" + merchantId + "/payment/" + paymentId
                        + "\"},\"merchant_owner\":{\"href\":\"" + BASE_PATH + "/" + merchantId + "\"}}}"
        );
    }

    private void createMerchant(boolean includePayments) {
        Merchant merchant = MerchantProvider.getMerchant();
        merchant.setId(null);
        if (includePayments) {
            merchant = MerchantProvider.attachPaymentsToMerchant(merchant);
            merchant.setOwner(activeUser);
            entityManager.persist(merchant);
            for (AbstractPayment payment : merchant.getPayments()) {
                payment.setId(null);
                payment.setMerchant(merchant);
                payment.setOwner(activeUser);
                entityManager.persist(payment);
            }
            return;
        }
        entityManager.persist(merchant);
    }
}
