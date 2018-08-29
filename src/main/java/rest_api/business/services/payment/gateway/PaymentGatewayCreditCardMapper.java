package rest_api.business.services.payment.gateway;

import org.springframework.stereotype.Component;
import rest_api.business.entities.payment.CreditCardType;
import java.util.EnumMap;

@Component
public class PaymentGatewayCreditCardMapper {

    private EnumMap<PaymentGatewayType, EnumMap<CreditCardType, String>> cardNameMapping;

    public PaymentGatewayCreditCardMapper() {
        cardNameMapping = new EnumMap<>(PaymentGatewayType.class);

        EnumMap<CreditCardType, String> bluepayMapping = new EnumMap<>(CreditCardType.class);

        EnumMap<CreditCardType, String> payeezyMapping = new EnumMap<>(CreditCardType.class);
        payeezyMapping.put(CreditCardType.AMERICAN_EXPRESS, "American Express");
        payeezyMapping.put(CreditCardType.MASTERCARD, "Mastercard");
        payeezyMapping.put(CreditCardType.VISA, "Visa");
        payeezyMapping.put(CreditCardType.JCB, "JCB");
        payeezyMapping.put(CreditCardType.DINERS_CLUB, "Diners Club");
        payeezyMapping.put(CreditCardType.DISCOVER, "Discover");

        cardNameMapping.put(PaymentGatewayType.Bluepay, bluepayMapping);
        cardNameMapping.put(PaymentGatewayType.Payeezy, payeezyMapping);
    }

    public String getCardName(PaymentGatewayType paymentGatewayType, CreditCardType creditCardType)
            throws CreditCardMappingException {
        EnumMap<CreditCardType, String> mapping = cardNameMapping.get(paymentGatewayType);
        if (mapping == null) {
            throw new CreditCardMappingException();
        }
        String cardName = mapping.get(creditCardType);
        if (cardName == null) {
            throw new CreditCardMappingException();
        }
        return cardName;
    }
}
