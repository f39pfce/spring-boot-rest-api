package rest_api.business.entities.base;

import rest_api.business.entities.user.User;
import lombok.Setter;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Provides repositories with owner field linking them to a creating user
 */
@MappedSuperclass
abstract public class AbstractUserOwnedEntity extends AbstractBaseEntity {

    /**
     * The owner and creator of the repositories
     */
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    protected User owner;

    /**
     * Getter, XmlTransient cannot be applied via Lombok
     *
     * @return Date owner
     */
    @XmlTransient
    public User getOwner() {
        return owner;
    }
}
