package rest_api.business.services.user;

import rest_api.business.entities.base.AbstractUserOwnedEntity;
import rest_api.presentation.security.UnauthorizedAccessException;
import rest_api.business.entities.user.User;

/**
 * Interface that defines the required functionality to work with user owned repositories
 */
public interface UserOwnedEntityService {

    /**
     * Test to determine if the API action on an repositories is being performed by that repositories's owner
     *
     * @param entity AbstractUserOwnedEntity
     * @return boolean
     */
    boolean isOwnerPerformingAction(AbstractUserOwnedEntity entity);

    /**
     * Return the authenticated user
     *
     * @return User authenticated user
     * @throws UnauthorizedAccessException when active user is not found or valid
     */
    User getActiveUser() throws UnauthorizedAccessException;
}
