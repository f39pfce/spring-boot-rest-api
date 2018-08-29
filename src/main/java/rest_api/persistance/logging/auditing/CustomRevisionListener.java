package rest_api.persistance.logging.auditing;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Listener that in conjunction with CustomRevision class adds custom fields to all revision logs
 */
public class CustomRevisionListener implements RevisionListener {

    /**
     * Records the user that performed the action by gathering their name from the security context
     *
     * @param revisionEntity the revision repositories
     */
    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevision rev = (CustomRevision) revisionEntity;
        rev.setUserName(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
