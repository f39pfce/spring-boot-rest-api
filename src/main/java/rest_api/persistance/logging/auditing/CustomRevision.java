package rest_api.persistance.logging.auditing;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Additional fields to log for each revision
 */
@Entity
@Table(name = "revision_metadata")
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevision extends DefaultRevisionEntity {

    /**
     * User name of user that caused the transaction
     */
    @Getter
    @Setter
    private String userName;
}