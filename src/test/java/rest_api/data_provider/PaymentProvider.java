package rest_api.data_provider;

import java.util.*;

import rest_api.business.entities.payment.AbstractPayment;
import rest_api.business.entities.payment.CreditCardPayment;
import rest_api.business.entities.user.User;
import rest_api.business.entities.merchant.Merchant;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PaymentProvider {

    public static List<AbstractPayment> getPayments() {
        ObjectMapper mapper = new ObjectMapper();

        Merchant merchant = new Merchant();
        merchant.setId(1L);

        User owner1 = new User();
        owner1.setId(1L);
        User owner2 = new User();
        owner2.setId(2L);

        List<Map<String, Object>> payments = new ArrayList<>();

        Map<String, Object> payment1 = new HashMap<>();
        payment1.put("id", "1");
        payment1.put("amount", "1.23");
        CreditCardPayment cc1 = new CreditCardPayment();
        cc1.setCvv("123");
        cc1.setExpirationMonth("01");
        cc1.setExpirationYear(2017);
        cc1.setCardNumber("4111111111111111");
        payment1.put("creditCard", cc1);
        payments.add(payment1);

        Map<String, Object> payment2 = new HashMap<>();
        payment2.put("id", "2");
        payment2.put("amount", "4.56");
        CreditCardPayment cc2 = new CreditCardPayment();
        cc2.setCvv("456");
        cc2.setExpirationMonth("06");
        cc2.setExpirationYear(2001);
        cc2.setCardNumber("5555555555555555");
        payment2.put("creditCard", cc2);
        payments.add(payment2);


        Map<String, Object> payment3 = new HashMap<>();
        payment3.put("id", "3");
        payment3.put("amount", "7.89");
        CreditCardPayment cc3 = new CreditCardPayment();
        cc3.setCvv("789");
        cc3.setExpirationMonth("03");
        cc3.setExpirationYear(2004);
        cc3.setCardNumber("1234123412341234");
        payment3.put("creditCard", cc3);
        payments.add(payment3);

        List<AbstractPayment> list = new ArrayList<>();
        for (Map<String, Object> payment : payments) {
            AbstractPayment paymentObject = mapper.convertValue(payment, AbstractPayment.class);
            paymentObject.setMerchant(merchant);
            paymentObject.setOwner(paymentObject.getId().equals(3L) ? owner2 : owner1);
            list.add(paymentObject);
        }
        return list;
    }

    public static AbstractPayment getPayment() {
        ObjectMapper mapper = new ObjectMapper();

        Merchant merchant = new Merchant();
        merchant.setId(1L);

        User owner = new User();
        owner.setId(1L);

        Map<String, Object> payment = new HashMap<>();
        payment.put("id", "1");
        payment.put("merchant", merchant);
        payment.put("amount", "1.23");
        CreditCardPayment cc = new CreditCardPayment();
        cc.setCvv("123");
        cc.setExpirationMonth("01");
        cc.setExpirationYear(2017);
        cc.setCardNumber("4111111111111111");
        payment.put("creditCard", cc);

        AbstractPayment paymentObject = mapper.convertValue(payment, AbstractPayment.class);
        paymentObject.setMerchant(merchant);
        paymentObject.setOwner(owner);
        return paymentObject;
    }
}
