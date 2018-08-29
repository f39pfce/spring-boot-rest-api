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
import rest_api.business.services.payment.event.PaymentDeleteEvent;
import rest_api.business.services.payment.gateway.PaymentGatewayNotFoundException;
import rest_api.business.services.payment.gateway.PaymentGatewayResolver;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PaymentDeleteEventListenerTest {

    private static AbstractPayment payment;
    private static PaymentDeleteEvent paymentDeleteEvent;

    @Autowired
    PaymentDeleteEventListener paymentDeleteEventListener;

    @MockBean
    PaymentGatewayResolver paymentGatewayResolver;

    @BeforeClass
    public static void setUp() {


        payment = new AbstractPayment();
        paymentDeleteEvent = new PaymentDeleteEvent(AbstractPayment.class, payment);
    }

    @Test
    public void givenAllCases_whenSaveMerchant_thenBoard() throws PaymentGatewayNotFoundException {
        //given that
        //all cases

        //then perform
        paymentDeleteEventListener.onApplicationEvent(paymentDeleteEvent);

        //and expect
        verify(paymentGatewayResolver, times(1)).getPaymentGateway(payment);
    }
}
