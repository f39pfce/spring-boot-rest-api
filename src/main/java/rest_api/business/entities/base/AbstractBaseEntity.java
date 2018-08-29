package rest_api.business.entities.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * Base repositories providing the ID, created and updated times for its children
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
abstract public class AbstractBaseEntity {

    /**
     * Entity ID
     */
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy= GenerationType.AUTO)
    protected Long id;

    /**
     * Created time
     */
    @Column(name = "created_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    protected Date createdAt;

    /**
     * Updated time
     */
    @Column(name = "updated_time", nullable = false)
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    protected Date updatedAt;

    /**
     * Getter, XmlTransient cannot be applied via Lombok
     *
     * @return Date updatedAt
     */
    @XmlTransient
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Getter, XmlTransient cannot be applied via Lombok
     *
     * @return Date createdAt
     */
    @XmlTransient
    public Date getCreatedAt() {
        return createdAt;
    }
}
