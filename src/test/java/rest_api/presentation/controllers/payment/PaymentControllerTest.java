package rest_api.presentation.controllers.payment;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import rest_api.business.entities.payment.AbstractPayment;
import rest_api.data_provider.PaymentProvider;
import rest_api.business.services.payment.PaymentServiceImpl;
import rest_api.presentation.security.UnauthorizedAccessException;
import rest_api.business.entities.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rest_api.presentation.controllers.merchant.MerchantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@EnableSpringDataWebSupport
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = PaymentController.class, secure = false)
public class PaymentControllerTest {

    private static String BASE_PATH = "http://localhost/v1/merchant";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PaymentServiceImpl paymentService;

    @Before
    public void setup() throws UnauthorizedAccessException {
        User user = new User();
        user.setId(1L);
        when(paymentService.getActiveUser()).thenReturn(user);
    }

    @Test
    public void givenNoMerchantsExist_whenFindAllPaymentsByMerchantId_thenReturnNotFound() throws Exception {
        //given that
        long merchantId = 1L;
        when(paymentService.findAllByMerchantId(merchantId)).thenThrow(new MerchantNotFoundException());

        //then perform
        mvc.perform(get("/v1/merchant/" + merchantId + "/payment")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();

        //and expect
        verify(paymentService, times(1)).findAllByMerchantId(merchantId);
        verify(paymentService, times(1)).getActiveUser();
    }

    @Test
    public void givenMerchantExists_whenFindAllPaymentsByMerchantId_thenReturnOnlyOwnedPayments() throws Exception {
        //given that
        long merchantId = 1L;
        when(paymentService.findAllByMerchantId(merchantId)).thenReturn(PaymentProvider.getPayments());
        when(paymentService.getPaymentDtoResource(any(AbstractPayment.class))).thenCallRealMethod();

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant/" + merchantId + "/payment")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //and expect
        verify(paymentService, times(1)).findAllByMerchantId(merchantId);
        verify(paymentService, times(1)).getActiveUser();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "[{\"id\":1,\"amount\":1.23,\"cardNumber\":\"4111111111111111\","
                        + "\"cvv\":\"123\",\"expirationYear\":2017,\"expirationMonth\":\"01\","
                        + "\"links\":[{\"rel\":\"self\",\"href\":\"" + BASE_PATH + "/1/payment/1\"},"
                        + "{\"rel\":\"merchant_owner\",\"href\":\"" + BASE_PATH + "/1\"}]},"

                + "{\"id\":2,\"amount\":4.56,\"cardNumber\":\"5555555555555555\","
                        + "\"cvv\":\"456\",\"expirationYear\":2001,\"expirationMonth\":\"06\","
                        + "\"links\":[{\"rel\":\"self\",\"href\":\"" + BASE_PATH + "/1/payment/2\"},"
                        + "{\"rel\":\"merchant_owner\",\"href\":\"" + BASE_PATH + "/1\"}]}]"
        );
    }

    @Test
    public void givenNonOwnerAccessToPayment_whenFindByMerchantAndPaymentIds_thenReturnUnauthorized() throws Exception {
        //given that
        long merchantId = 1L;
        long paymentId  = 1L;
        when(paymentService.findOneByMerchantIdAndPaymentId(merchantId, paymentId))
                .thenReturn(PaymentProvider.getPayment());
        when(paymentService.isOwnerPerformingAction(any(AbstractPayment.class))).thenReturn(false);

        //then perform
        mvc.perform(get("/v1/merchant/" + merchantId + "/payment/" + paymentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        //and expect
        verify(paymentService, times(1)).isOwnerPerformingAction(any(AbstractPayment.class));
    }

    @Test
    public void givenMerchantDoesNotExist_whenFindByMerchantAndPaymentIds_thenReturnNotFound() throws Exception {
        //given that
        long merchantId = 1L;
        long paymentId  = 1L;
        when(paymentService.findOneByMerchantIdAndPaymentId(merchantId, paymentId))
                .thenThrow(new MerchantNotFoundException());

        //then perform
        mvc.perform(get("/v1/merchant/" + merchantId + "/payment/" + paymentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //and expect
        verify(paymentService, times(1)).findOneByMerchantIdAndPaymentId(merchantId, paymentId);
        verify(paymentService, times(0)).isOwnerPerformingAction(any(AbstractPayment.class));
    }

    @Test
    public void givenPaymentDoesNotExist_whenFindByMerchantAndPaymentIds_thenReturnNotFound() throws Exception {
        //given that
        long merchantId = 1L;
        long paymentId  = 1L;
        when(paymentService.findOneByMerchantIdAndPaymentId(merchantId, paymentId))
                .thenThrow(new PaymentNotFoundException());

        //then perform
        mvc.perform(get("/v1/merchant/" + merchantId + "/payment/" + paymentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //and expect
        verify(paymentService, times(1)).findOneByMerchantIdAndPaymentId(merchantId, paymentId);
        verify(paymentService, times(0)).isOwnerPerformingAction(any(AbstractPayment.class));
    }

    @Test
    public void givenPaymentAndMerchantExist_whenFindByMerchantAndPaymentIds_thenReturnPayment() throws Exception {
        //given that
        long merchantId = 1L;
        long paymentId  = 1L;
        AbstractPayment payment = PaymentProvider.getPayment();
        when(paymentService.findOneByMerchantIdAndPaymentId(merchantId, paymentId))
                .thenReturn(payment);
        when(paymentService.isOwnerPerformingAction(payment)).thenReturn(true);
        when(paymentService.getPaymentDtoResource(payment)).thenReturn(
                new Resource<>(
                        new AbstractPaymentDto(payment),
                        new Link(BASE_PATH + "/" + merchantId + "/" + "payment" + "/" + paymentId, "self"),
                        new Link(BASE_PATH + "/" + merchantId, "merchant_owner")
                )
        );

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant/" + merchantId + "/payment/" + paymentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //and expect
        verify(paymentService, times(1)).findOneByMerchantIdAndPaymentId(merchantId, paymentId);
        verify(paymentService, times(1)).isOwnerPerformingAction(payment);
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "{\"id\":1,\"amount\":1.23,\"cardNumber\":\"4111111111111111\","
                        + "\"cvv\":\"123\",\"expirationYear\":2017,\"expirationMonth\":\"01\","
                        + "\"_links\":{\"self\":{\"href\":\"" + BASE_PATH + "/" + merchantId + "/payment/" + paymentId
                        + "\"},\"merchant_owner\":{\"href\":\"" + BASE_PATH + "/1\"}}}"
        );
    }

    @Test
    public void givenNonOwnerAction_whenDeletePayment_thenReturnNotFound() throws Exception {
        //given that
        long merchantId = 1L;
        long paymentId  = 1L;
        when(paymentService.isOwnerPerformingAction(any(AbstractPayment.class))).thenReturn(false);

        //then perform
        mvc.perform(delete("/v1/merchant/" + merchantId + "/payment/" + paymentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();

        //and expect
        verify(paymentService, times(1)).findOneByMerchantIdAndPaymentId(merchantId, paymentId);
        verify(paymentService, times(1)).isOwnerPerformingAction(any(AbstractPayment.class));
        verify(paymentService, times(0)).delete(paymentId);
    }

    @Test
    public void givenPaymentDoesNotExist_whenDeletePayment_thenThrowPaymentNotFoundException() throws Exception {
        //given that
        long merchantId = 1L;
        long paymentId  = 1L;
        when(paymentService.findOneByMerchantIdAndPaymentId(merchantId, paymentId))
                .thenThrow(new PaymentNotFoundException());
        when(paymentService.isOwnerPerformingAction(any(AbstractPayment.class))).thenReturn(true);

        //then perform
        mvc.perform(delete("/v1/merchant/" + merchantId + "/payment/" + paymentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();

        //and expect
        verify(paymentService, times(0)).isOwnerPerformingAction(any(AbstractPayment.class));
        verify(paymentService, times(1)).findOneByMerchantIdAndPaymentId(merchantId, paymentId);
        verify(paymentService, times(0)).delete(paymentId);
    }

    @Test
    public void givenMerchantDoesNotExist_whenDeletePayment_thenThrowMerchantNotFoundException() throws Exception {
        //given that
        long merchantId = 1L;
        long paymentId  = 1L;
        when(paymentService.findOneByMerchantIdAndPaymentId(merchantId, paymentId))
                .thenThrow(new MerchantNotFoundException());
        when(paymentService.isOwnerPerformingAction(any(AbstractPayment.class))).thenReturn(true);

        //then perform
        mvc.perform(delete("/v1/merchant/" + merchantId + "/payment/" + paymentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();

        //and expect
        verify(paymentService, times(0)).isOwnerPerformingAction(any(AbstractPayment.class));
        verify(paymentService, times(1)).findOneByMerchantIdAndPaymentId(merchantId, paymentId);
        verify(paymentService, times(0)).delete(paymentId);
    }

    @Test
    public void givenMerchantAndPaymentExist_whenDeletePayment_succeedInDeletingPayment() throws Exception {
        //given that
        long merchantId = 1L;
        long paymentId  = 1L;
        when(paymentService.isOwnerPerformingAction(any(AbstractPayment.class))).thenReturn(true);

        //then perform
        mvc.perform(delete("/v1/merchant/" + merchantId + "/payment/" + paymentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //and expect
        verify(paymentService, times(1)).isOwnerPerformingAction(any(AbstractPayment.class));
        verify(paymentService, times(1)).findOneByMerchantIdAndPaymentId(merchantId, paymentId);
        verify(paymentService, times(1)).delete(paymentId);
    }

    @Test
    public void givenMerchantDoesNotExist_whenCreatePayment_throwMerchantNotFoundException() throws Exception {
        //given that
        long merchantId = 1L;
        doThrow(new MerchantNotFoundException()).when(paymentService).save(eq(merchantId), any(AbstractPayment.class));

        //then perform
        mvc.perform(post("/v1/merchant/" + merchantId + "/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"amount\":5.50, \"cardNumber\":4111111111111111," +
                        "\"cvv\":119,\"expirationMonth\":\"10\",\"expirationYear\":2017}"
                )
        ).andExpect(status().isNotFound()).andReturn();

        //and expect
        verify(paymentService, times(1)).getActiveUser();
        verify(paymentService, times(1)).save(eq(merchantId), any(AbstractPayment.class));
        verify(paymentService, times(0)).getPaymentDtoResource(any(AbstractPayment.class));
    }

    @Test
    public void givenMerchantExists_whenCreatePayment_thenSaveSuccessfully() throws Exception {
        //given that
        long merchantId = 1L;
        long paymentId  = 1L;
        AbstractPayment payment = PaymentProvider.getPayment();
        when(paymentService.save(eq(merchantId), any(AbstractPayment.class))).thenReturn(payment);
        when(paymentService.getPaymentDtoResource(payment)).thenReturn(
                new Resource<>(
                        new AbstractPaymentDto(payment),
                        new Link(BASE_PATH + "/" + merchantId + "/" + "payment" + "/" + paymentId, "self"),
                        new Link(BASE_PATH + "/" + merchantId, "merchant_owner")
                )
        );

        //then perform
        MvcResult result = mvc.perform(post("/v1/merchant/" + merchantId + "/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":5.50, \"cardNumber\":4111111111111111," +
                        "\"cvv\":116,\"expirationMonth\":\"02\",\"expirationYear\":2018}"
                )
        ).andExpect(status().isOk()).andReturn();

        //and expect
        verify(paymentService, times(1)).getActiveUser();
        verify(paymentService, times(1)).save(eq(merchantId), any(AbstractPayment.class));
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "{\"id\":1,\"amount\":1.23,\"cardNumber\":\"4111111111111111\","
                        + "\"cvv\":\"123\",\"expirationYear\":2017,\"expirationMonth\":\"01\","
                        + "\"_links\":{\"self\":{\"href\":\"" + BASE_PATH + "/" + merchantId + "/payment/" + paymentId
                        + "\"},\"merchant_owner\":{\"href\":\"" + BASE_PATH + "/1\"}}}"
        );
    }
}
