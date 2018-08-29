package rest_api.presentation.security.oauth;

import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Object representation of a jwt string
 */
@XmlRootElement(name = "token")
public class JwtToken {

    /**
     * Token value
     */
    @Getter
    @XmlElement
    private String value;

    /**
     * Constructor
     *
     * Empty constructor required for XML serialization
     */
    public JwtToken() {}

    /**
     * Constructor
     *
     * @param value token value
     */
    public JwtToken(String value) {
        this.value = value;
    }
}
