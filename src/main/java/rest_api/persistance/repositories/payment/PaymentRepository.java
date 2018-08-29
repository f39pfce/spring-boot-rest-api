package rest_api.persistance.repositories.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rest_api.business.entities.payment.AbstractPayment;

/**
 * JPA repository for Merchant repositories with Long ID
 */
@Repository
public interface PaymentRepository extends JpaRepository<AbstractPayment, Long> {}
