package rest_api.persistance.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rest_api.business.entities.user.User;

import java.util.Optional;

/**
 * JPA repository for User repositories with Long ID
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find individual user by their user name
     *
     * @param userName user name
     * @return Optional<User>
     */
    Optional<User> findOneByUserName(String userName);
}
