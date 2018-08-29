package rest_api.business.services.payment.gateway;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import rest_api.business.entities.payment.AbstractPayment;

/**
 * Determines which PaymentGateway to use
 */
@Component
public class PaymentGatewayResolver implements ApplicationContextAware {

    /**
     * Application context
     */
    private ApplicationContext applicationContext;

    /**
     * Setter
     *
     * @param applicationContext application context
     * @throws BeansException on app. context failure
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Resolve which payment boarding is used based on the boarding type of the payment's owning merchant
     *
     * @param payment CreditCardPayment
     * @return PaymentGateway
     * @throws PaymentGatewayNotFoundException if no boarding found
     */
    public PaymentGateway getPaymentGateway(AbstractPayment payment) throws PaymentGatewayNotFoundException {
        //TODO look at this, maybe I could use a map instead with getGatewayType as key since these always instantiated
        if (payment.getMerchant().getGatewayType().equals(PaymentGatewayType.Bluepay)) {
            return this.applicationContext.getBean(BluepayGateway.class);
        } else if (payment.getMerchant().getGatewayType().equals(PaymentGatewayType.Payeezy)) {
            return this.applicationContext.getBean(PayEezyGateway.class);
        } else {
            throw new PaymentGatewayNotFoundException();
        }
    }
}
