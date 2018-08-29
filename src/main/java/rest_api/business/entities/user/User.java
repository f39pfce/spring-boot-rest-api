package rest_api.business.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;
import rest_api.business.entities.base.AbstractBaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import lombok.Getter;

/**
 * Entity that represents an API user
 */
@Entity
@Getter
@Table(name = "users")
@JsonIgnoreProperties({"createdAt", "updatedAt"})
public class User extends AbstractBaseEntity implements Serializable {

    //TODO currently unused, allow admin to turn off accounts
    /**
     * If the user is currently flagged as active
     */
    private boolean active;

    /**
     * API user user name
     */
    @NotBlank
    @Column(unique = true)
    private String userName;

    /**
     * API user secret key
     */
    @NotBlank
    private String secretKey;

    /**
     * Constructor
     */
    public User() {
    }

    /**
     * Constructor
     *
     * @param userName user name
     * @param password secret key
     */
    public User(String userName, String password) {
        this.active       = true;
        this.userName     = userName;
        this.secretKey    = password;
    }
}
