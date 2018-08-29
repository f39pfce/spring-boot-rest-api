package rest_api.business.services.user;

import java.util.Optional;

import rest_api.business.entities.base.AbstractUserOwnedEntity;
import rest_api.presentation.security.UnauthorizedAccessException;
import rest_api.business.entities.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Provide an implementation for business functions required to interact with user owned repositories
 */
abstract public class UserOwnedEntityServiceImpl implements UserOwnedEntityService {

    /**
     * Service responsible for User repositories interactions
     */
    private UserService userService;

    /**
     * Constructor
     *
     * @param userService business responsible for User repositories interactions
     */
    @Autowired
    public UserOwnedEntityServiceImpl(UserService userService) {
        this.userService = userService;
    }

    /**
     * Determine if the active user matches that of the user owned repositories
     *
     * @param entity AbstractUserOwnedEntity
     * @return boolean
     */
    public boolean isOwnerPerformingAction(AbstractUserOwnedEntity entity) {
        boolean ownerPerformed;
        try {
            ownerPerformed = getActiveUser().getId().equals(entity.getOwner().getId());
        } catch (NullPointerException | UnauthorizedAccessException e) {
            return false;
        }
        return ownerPerformed;
    }

    /**
     * Find a used based on the information stored in the security context
     *
     * @return User the authorized user
     * @throws UnauthorizedAccessException thrown if either security context is empty, or user is not found
     */
    public User getActiveUser() throws UnauthorizedAccessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedAccessException();
        }
        Optional<User> user = userService.findOneByUserName(authentication.getName());
        return user.orElseThrow(UnauthorizedAccessException::new);
    }
}
