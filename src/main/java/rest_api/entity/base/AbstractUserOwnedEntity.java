package rest_api.entity.base;

import rest_api.entity.user.User;
import lombok.Setter;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Provides entity with owner field linking them to a creating user
 */
@MappedSuperclass
abstract public class AbstractUserOwnedEntity extends AbstractBaseEntity {

    /**
     * The owner and creator of the entity
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
