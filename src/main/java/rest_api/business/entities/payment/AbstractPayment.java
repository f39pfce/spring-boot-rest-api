package rest_api.business.entities.payment;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import rest_api.business.entities.base.AbstractUserOwnedEntity;
import rest_api.business.entities.merchant.Merchant;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.envers.Audited;

/**
 * Entity representation of a credit card
 */
@Audited
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "payment_base")
@XmlAccessorType(XmlAccessType.FIELD)
abstract public class AbstractPayment extends AbstractUserOwnedEntity {

    /**
     * Payment type
     */
    @NotNull
    @XmlTransient
    protected PaymentType paymentType;

    /**
     * Class of repositories DTO
     */
    @NotNull
    @XmlTransient
    protected Class entityDtoClass;

    /**
     * Merchant owner of the repositories
     */
    @NotNull
    @ManyToOne
    @XmlTransient
    @JoinColumn(name = "merchant", nullable = false)
    protected Merchant merchant;

    /**
     * Amount of the payment
     */
    @NotNull
    protected double amount;

    /**
     * Return class of repositories DTO
     *
     * @return Class
     */
    final public Class getDtoEntityClass() {
        return entityDtoClass;
    }
}
