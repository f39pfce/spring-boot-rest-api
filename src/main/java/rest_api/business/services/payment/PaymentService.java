package rest_api.business.services.payment;

import java.util.List;

import org.springframework.hateoas.Resource;
import rest_api.business.services.user.UserOwnedEntityService;
import rest_api.business.entities.payment.AbstractPayment;
import rest_api.presentation.controllers.merchant.MerchantNotFoundException;
import rest_api.business.dtos.payment.AbstractPaymentDto;
import rest_api.presentation.controllers.payment.PaymentNotFoundException;
import rest_api.presentation.security.UnauthorizedAccessException;

/**
 * Service for interactions with the Merchant Entity
 */
public interface PaymentService extends UserOwnedEntityService {

    /**
     * Persist payment repositories
     *
     * @param merchantId ID of merchant payment is attached to
     * @param paymentDto payment repositories
     * @return CreditCardPayment
     */
    AbstractPayment save(Long merchantId, AbstractPaymentDto paymentDto)
            throws MerchantNotFoundException, UnauthorizedAccessException;

    /**
     * Provide a list of all payments owned by a specific merchant
     *
     * @param merchantId ID of merchant
     * @return List<CreditCardPayment> list of payments owned by that merchant
     * @throws MerchantNotFoundException thrown if merchant by ID does not exist
     */
    List<AbstractPayment> findAllByMerchantId(long merchantId) throws MerchantNotFoundException;

    /**
     * Provide a single payments owned by a specific merchant identified by payment ID
     *
     * @param merchantId ID of merchant
     * @param paymentId ID of payment
     * @return CreditCardPayment identified by both merchant and payment IDs
     * @throws MerchantNotFoundException thrown if merchant by ID does not exist
     * @throws PaymentNotFoundException thrown if payment by ID does not exist
     */
    AbstractPayment findOneByMerchantIdAndPaymentId(long merchantId, long paymentId) throws MerchantNotFoundException,
            PaymentNotFoundException;

    //TODO - this should probably include the merchant ID for confirmation of ownership
    /**
     * Delete a payment
     *
     * @param id payment ID
     * @throws PaymentNotFoundException if payment by ID does not exist
     */
    void delete(long id) throws PaymentNotFoundException;

    /**
     * Convert CreditCardPayment repositories to Resource<AbstractPaymentDto> repositories
     *
     * @param payment payment to be converted
     * @return Resource<AbstractPaymentDto>
     */
    Resource<AbstractPaymentDto> getPaymentDtoResource(AbstractPayment payment);
}
