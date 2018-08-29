package rest_api.business.services.payment.gateway;

import lombok.Getter;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rest_api.config.PropertiesConfig;
import rest_api.business.entities.payment.AbstractPayment;
import java.io.IOException;
import java.security.SecureRandom;
import org.apache.http.Header;
import rest_api.business.entities.payment.CreditCardPayment;
import rest_api.business.entities.payment.PaymentType;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;

/**
 * PayEezy payment boarding
 */
@Component
public class PayEezyGateway extends AbstractPaymentGateway {

    /**
     * CreditCardPayment boarding type
     */
    final private PaymentGatewayType paymentGatewayType = PaymentGatewayType.Payeezy;

    /**
     * Properties
     */
    private PropertiesConfig propertiesConfig;

    /**
     * Secure random number generator
     */
    private SecureRandom secureRandom;

    /**
     * Constructor
     *
     * @param propertiesConfig properties
     */
    @Autowired
    public PayEezyGateway(
            PaymentGatewayCreditCardMapper paymentGatewayCreditCardMapper,
            PropertiesConfig propertiesConfig,
            SecureRandom secureRandom) {

        super(paymentGatewayCreditCardMapper);
        this.propertiesConfig = propertiesConfig;
        this.secureRandom     = secureRandom;
    }

    /**
     * Return the payment boarding type
     *
     * @return PaymentGatewayType
     */
    public PaymentGatewayType getType() {
        return this.paymentGatewayType;
    }

    /**
     * Board payment to the boarding
     *
     * @param payment CreditCardPayment
     */
    public void board(AbstractPayment payment) throws IOException {

        PropertiesConfig.PayeezyGateway payEezy = propertiesConfig.getPayeezyGateway();
        //stubbed here as most likely API credentials on merchant per merchant basis
        String nonce     = Long.toString(secureRandom.nextLong());
        long timestamp   = System.currentTimeMillis();
        String apiKey    = payEezy.getApiKey();
        String apiSecret = payEezy.getApiSecret();
        String token     = payEezy.getToken();

        try {
            String payload = getPayload(payment);
            BoardingContext context = new BoardingContext(apiKey, apiSecret, token, nonce, payload, timestamp);
            String hmacToken = generateHmac(context);
            doPost(context, hmacToken);
        } catch (ClassCastException e) {
            //TODO create log for failure to board, notify customer in API or email
        } catch (CreditCardMappingException e) {
            //TODO create log for failure to board, notify customer in API or email
        }
    }

    private String generateHmac(BoardingContext context) {
        try {
            String message = context.getApiUser() + context.getNonce() + context.getTimestamp()
                    + context.getToken() + context.getPayload();
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(context.getApiSecret().getBytes(), "HmacSHA256");
            sha256HMAC.init(secretKey);
            return encodeBase64String(Hex.encodeHexString(sha256HMAC.doFinal(message.getBytes())).getBytes());
        }
        catch (Exception e){
            System.out.println("Error");
        }
        return "";
    }

    private void doPost(BoardingContext context, String hmacToken) throws IOException {
        HttpClient httpClientLead = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(propertiesConfig.getPayeezyGateway().getUrl());
        httpPost.setHeaders(
                new Header[] {
                        new BasicHeader("apikey", context.getApiUser()),
                        new BasicHeader("token", context.getToken()),
                        new BasicHeader("Content-type", "application/json"),
                        new BasicHeader("Authorization", hmacToken),
                        new BasicHeader("nonce", context.getNonce()),
                        new BasicHeader("timestamp", Long.toString(context.getTimestamp()))
                }
        );

        httpPost.setEntity(new StringEntity(context.getPayload()));
        HttpResponse response = httpClientLead.execute(httpPost);
        response.toString();
    }

    private String getPayload(AbstractPayment payment) throws ClassCastException, CreditCardMappingException {
        if (payment.getPaymentType().equals(PaymentType.CREDIT_CARD)) {
            CreditCardPayment creditCardPayment = (CreditCardPayment) payment;
            String cardType = paymentGatewayCreditCardMapper.getCardName(
                    this.paymentGatewayType,
                    creditCardPayment.getCardType()
            );
            String amount = Double.toString(creditCardPayment.getAmount()).replace(".", "");
            return "{" +
                    "  \"transaction_type\": \"purchase\"," +
                    "  \"method\": \"credit_card\"," +
                    "  \"amount\": \"" + amount + "\"," +
                    "  \"currency_code\": \"USD\"," +
                    "  \"credit_card\": {" +
                    "    \"type\": \"" + cardType +" \"," +
                    "    \"cardholder_name\": \""+ creditCardPayment.getCardholderName() +"\"," +
                    "    \"card_number\": \"" + creditCardPayment.getCardNumber() + "\"," +
                    "    \"exp_date\": \""+ creditCardPayment.getExpirationMonth()
                            + creditCardPayment.getExpirationYear().substring(2) +"\"," +
                    "    \"cvv\": \""+ creditCardPayment.getCvv() +"\"" +
                    "  }" +
                    "}";
        } else if (payment.getPaymentType().equals(PaymentType.ACH)) {
            return "";
        }
    }

    @Getter
    private class BoardingContext {
        private String apiUser;
        private String apiSecret;
        private String token;
        private String nonce;
        private String payload;
        private long timestamp;

        private BoardingContext(
                String apiUser,
                String apiSecret,
                String token,
                String nonce,
                String payload,
                long timestamp) {

            this.apiUser   = apiUser;
            this.apiSecret = apiSecret;
            this.token     = token;
            this.nonce     = nonce;
            this.payload   = payload;
            this.timestamp = timestamp;
        }
    }
}
