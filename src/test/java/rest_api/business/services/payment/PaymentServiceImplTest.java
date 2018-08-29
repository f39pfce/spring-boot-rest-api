package rest_api.business.services.payment;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.springframework.boot.test.context.SpringBootTest;
import rest_api.Application;
import rest_api.business.entities.payment.AbstractPayment;
import rest_api.business.entities.merchant.Merchant;
import rest_api.data_provider.PaymentProvider;
import rest_api.persistance.repositories.payment.PaymentRepository;
import rest_api.presentation.controllers.merchant.MerchantNotFoundException;
import rest_api.data_provider.MerchantProvider;
import rest_api.persistance.repositories.merchant.MerchantRepository;
import rest_api.business.services.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import rest_api.presentation.controllers.payment.CreditCardPaymentDto;
import rest_api.presentation.controllers.payment.PaymentNotFoundException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PaymentServiceImplTest {

    @Autowired
    private PaymentService paymentService;

    @MockBean
    private MerchantRepository merchantRepository;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private UserService userService;

    @Test
    public void givenMerchantExists_whenSavePayment_thenSucceed() throws MerchantNotFoundException {
        //given
        long merchantId = 1L;
        Merchant merchant = MerchantProvider.getMerchant();
        when(merchantRepository.findOne(merchantId)).thenReturn(merchant);

        //then perform
        paymentService.save(merchantId, new CreditCardPaymentDto(PaymentProvider.getPayment()));

        //and expect
        ArgumentCaptor<AbstractPayment> argument = ArgumentCaptor.forClass(AbstractPayment.class);
        verify(merchantRepository, times(1)).findOne(eq(merchantId));
        verify(paymentRepository, times(1)).save(argument.capture());
        assertThat(argument.getValue().getMerchant()).isEqualTo(merchant);
    }

    @Test(expected = MerchantNotFoundException.class)
    public void givenMerchantDoesNotExist_whenSavePayment_thenThrowMerchantNotFoundException()
            throws MerchantNotFoundException {
        //given
        long merchantId = 1L;
        when(merchantRepository.findOne(merchantId)).thenReturn(null);

        //then perform
        paymentService.save(merchantId, PaymentProvider.getPayment());

        //and expect
        verify(merchantRepository, times(1)).findOne(eq(merchantId));
        verify(paymentRepository, times(0)).save(any(AbstractPayment.class));
    }

    @Test(expected = MerchantNotFoundException.class)
    public void givenMerchantDoesNotExist_whenFindPaymentsByMerchant_thenThrowMerchantNotFoundException()
            throws MerchantNotFoundException {
        //given
        long merchantId = 1L;
        when(merchantRepository.findOne(merchantId)).thenReturn(null);

        //then perform
        paymentService.findAllByMerchantId(merchantId);

        //and expect
        verify(merchantRepository, times(1)).findOne(merchantId);
    }

    @Test
    public void givenMerchantExists_whenFindPaymentsByMerchant_thenReturnPayments()
            throws MerchantNotFoundException {
        //given
        long merchantId = 1L;
        Merchant merchant = MerchantProvider.getMerchant();
        merchant = MerchantProvider.attachPaymentsToMerchant(merchant);
        when(merchantRepository.findOne(merchantId)).thenReturn(merchant);

        //then perform
        List<AbstractPayment> payments = paymentService.findAllByMerchantId(merchantId);

        //and expect
        verify(merchantRepository, times(1)).findOne(merchantId);
        //and expect
        assertThat(payments).hasSize(3);
        assertThat(payments).extracting("amount", "merchant")
                .contains(
                        tuple(1.23, merchant),
                        tuple(4.56, merchant),
                        tuple(7.89, merchant)
                );
    }

    @Test(expected = MerchantNotFoundException.class)
    public void givenMerchantDoesNotExist_whenFindPaymentByMerchantAndPaymentId_thenThrowMerchantNotFoundException()
            throws MerchantNotFoundException, PaymentNotFoundException {
        //given
        long merchantId = 1L;
        long paymentId  = 1L;
        when(merchantRepository.findOne(merchantId)).thenReturn(null);

        //then perform
        paymentService.findOneByMerchantIdAndPaymentId(merchantId, paymentId);

        //and expect
        verify(merchantRepository, times(1)).findOne(merchantId);
    }

    @Test
    public void givenMerchantAndPaymentExist_whenFindPaymentByMerchantAndPaymentId_thenReturnPayment()
            throws MerchantNotFoundException, PaymentNotFoundException {
        //given
        long merchantId = 1L;
        long paymentId  = 1L;
        Merchant merchant = MerchantProvider.getMerchant();
        AbstractPayment payment = PaymentProvider.getPayment();
        payment.setMerchant(merchant);
        //we need to use reflection to set the private field of Payments here
        try {
            Field payments = Class.forName("rest_api.business.entities.merchant.Merchant").getDeclaredField("payments");
            payments.setAccessible(true);
            ArrayList<AbstractPayment> paymentList = new ArrayList<>();
            paymentList.add(payment);
            payments.set(merchant, paymentList);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to reflect the merchant class.");
        }
        when(merchantRepository.findOne(merchantId)).thenReturn(merchant);

        //then perform
        AbstractPayment returnedPayment = paymentService.findOneByMerchantIdAndPaymentId(merchantId, paymentId);

        //and expect
        verify(merchantRepository, times(1)).findOne(merchantId);
        assertThat(returnedPayment).isEqualTo(payment);
    }

    @Test(expected = PaymentNotFoundException.class)
    public void givenPaymentDoesNotExists_whenFindPaymentByMerchantAndPaymentId_thenThrowException()
            throws MerchantNotFoundException, PaymentNotFoundException {
        //given
        long merchantId = 1L;
        long paymentId  = 1L;
        when(merchantRepository.findOne(merchantId)).thenReturn(MerchantProvider.getMerchant());

        //then perform
        paymentService.findOneByMerchantIdAndPaymentId(merchantId, paymentId);

        //and expect
        verify(merchantRepository, times(1)).findOne(merchantId);
        verify(paymentRepository, times(1)).findOne(paymentId);
    }

    @Test(expected = PaymentNotFoundException.class)
    public void givenPaymentDoesNotExist_whenDeletePayment_thenThrowException()
            throws PaymentNotFoundException {
        //given
        long paymentId = 1L;

        //then perform
        paymentService.delete(paymentId);

        //and expect
        verify(paymentRepository, times(1)).delete(paymentId);
    }
}
