package rest_api.persistance.repositories.merchant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rest_api.business.entities.merchant.Merchant;

/**
 * JPA repository for Merchant repositories with Long ID
 */
@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {}
