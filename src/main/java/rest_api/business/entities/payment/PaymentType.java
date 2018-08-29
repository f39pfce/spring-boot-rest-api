package rest_api.business.entities.payment;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentType {
    CREDIT_CARD, ACH;

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
