package rest_api.business.entities.payment;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CreditCardType {
    VISA, MASTERCARD, AMERICAN_EXPRESS, DISCOVER, JCB, DINERS_CLUB;

    /**
     * How enum is represented in JSON form
     *
     * @return String
     */
    @JsonValue
    public String toString() {
        return super.toString();
    }
}
