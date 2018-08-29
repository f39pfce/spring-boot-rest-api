package rest_api.business.services.payment.event.listener;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import rest_api.Application;
import rest_api.business.entities.payment.AbstractPayment;
import rest_api.business.entities.payment.CreditCardPayment;
import rest_api.business.services.payment.event.PaymentSaveEvent;
import rest_api.business.services.payment.gateway.PaymentGatewayNotFoundException;
import rest_api.config.PropertiesConfig;
import rest_api.business.services.payment.gateway.PaymentGatewayResolver;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PaymentSaveEventListenerTest {

    private static AbstractPayment payment;
    private static PaymentSaveEvent paymentSaveEvent;

    @Autowired
    PaymentSaveEventListener paymentSaveEventListener;

    @MockBean
    PaymentGatewayResolver paymentGatewayResolver;

    @Autowired
    PropertiesConfig propertiesConfig;

    @BeforeClass
    public static void setUp() {
        payment = new CreditCardPayment();
        paymentSaveEvent = new PaymentSaveEvent(AbstractPayment.class, payment);
    }

    @Test
    public void givenBoardingIsOn_whenSaveMerchant_thenBoard() throws PaymentGatewayNotFoundException {
        //given that
        propertiesConfig.setIpgBoardOnSave(true);

        //then perform
        paymentSaveEventListener.onApplicationEvent(paymentSaveEvent);

        //and expect
        verify(paymentGatewayResolver, times(1)).getPaymentGateway(payment);
    }

    @Test
    public void givenBoardingIsOff_whenSaveMerchant_thenDoNotBoard() throws PaymentGatewayNotFoundException {
        //given that
        propertiesConfig.setIpgBoardOnSave(false);

        //then perform
        paymentSaveEventListener.onApplicationEvent(paymentSaveEvent);

        //and expect
        verify(paymentGatewayResolver, times(0)).getPaymentGateway(payment);
    }
}
