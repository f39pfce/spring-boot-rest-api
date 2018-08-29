package rest_api.business.dtos.user;

import lombok.Getter;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data transfer object for User repositories
 */
@XmlRootElement(name = "user")
public class UserDto {

    /**
     * User name
     */
    @NotNull
    @Getter
    @XmlElement
    private String userName;

    /**
     * Secret key
     */
    @Getter
    @XmlElement
    private String secretKey;

    /**
     * Constructor
     *
     * Empty constructor required for XML serialization
     */
    public UserDto() {}

    /**
     * Constructor
     *
     * @param userName user name
     * @param secretKey secret key
     */
    public UserDto(String userName, String secretKey) {
        this.userName = userName;
        this.secretKey = secretKey;
    }
}
