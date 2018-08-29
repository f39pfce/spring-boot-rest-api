package rest_api.presentation.controllers.payment;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.hateoas.Resource;
import rest_api.business.dtos.payment.AbstractPaymentDto;
import rest_api.business.dtos.payment.PaymentDtoResources;
import rest_api.business.entities.payment.AbstractPayment;
import rest_api.business.services.payment.PaymentService;
import rest_api.presentation.controllers.merchant.MerchantNotFoundException;
import rest_api.presentation.security.UnauthorizedAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for performing actions using CreditCardPayment repositories
 */
@RestController
@RequestMapping("/v1/merchant/{merchantId}/payment")
public class PaymentController {

    /**
     * Service for interactions with CreditCardPayment repositories
     */
    private PaymentService paymentService;

    /**
     * Constructor
     *
     * @param paymentService business for interactions with CreditCardPayment repositories
     */
    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Action to create a new CreditCardPayment repositories
     *
     * @param merchantId ID of owning merchant
     * @param paymentDto payment DTO
     * @return Resource<AbstractPaymentDto>
     * @throws MerchantNotFoundException thrown when merchant not found by ID
     * @throws UnauthorizedAccessException thrown when non owner attempts to perform action
     */
    @PostMapping
    public Resource<AbstractPaymentDto> create(
            @PathVariable long merchantId,
            @RequestBody AbstractPaymentDto paymentDto
    ) throws MerchantNotFoundException, UnauthorizedAccessException {

        AbstractPayment payment = paymentService.save(merchantId, paymentDto);
        return paymentService.getPaymentDtoResource(payment);
    }

    /**
     * Find all payments owned by a specific merchant
     *
     * @param merchantId the ID of the merchant
     * @param pageable page and count supplied in URL
     * @return PaymentDtoResources XML serializable wrapper for List<Resource<AbstractPaymentDto>>
     * @throws MerchantNotFoundException thrown when merchant is not found by ID
     * @throws UnauthorizedAccessException thrown when non owner attempts to perform action
     */
    @GetMapping
    public PaymentDtoResources find(@PathVariable long merchantId, Pageable pageable)
            throws MerchantNotFoundException, UnauthorizedAccessException {

        long id = paymentService.getActiveUser().getId();
        List<AbstractPayment> payments = paymentService.findAllByMerchantId(merchantId);
        payments = payments.stream().filter(x -> x.getOwner().getId()
                .equals(id)).collect(Collectors.toList());

        int start = pageable.getOffset();
        int end = (start + pageable.getPageSize()) > payments.size() ?
                payments.size() : (start + pageable.getPageSize());
        Page<AbstractPayment> page = new PageImpl<>(payments.subList(start, end), pageable, payments.size());

        return new PaymentDtoResources(
                page.getContent().stream().map(paymentService::getPaymentDtoResource).collect(Collectors.toList())
        );
    }

    /**
     * Find a payment by merchant ID and payment ID
     *
     * @param merchantId merchant ID of owning merchant
     * @param paymentId payment ID
     * @return Resource<AbstractPaymentDto>
     * @throws MerchantNotFoundException thrown when merchant not found by ID
     * @throws PaymentNotFoundException thrown when payment not found by ID
     * @throws UnauthorizedAccessException thrown when non-owner attempts action
     */
    @GetMapping(value= "/{paymentId}")
    public Resource<AbstractPaymentDto> find(@PathVariable long merchantId, @PathVariable long paymentId)
            throws MerchantNotFoundException, PaymentNotFoundException, UnauthorizedAccessException {

        AbstractPayment payment = paymentService.findOneByMerchantIdAndPaymentId(merchantId, paymentId);
        if (!paymentService.isOwnerPerformingAction(payment)) {
            throw new UnauthorizedAccessException();
        }
        return paymentService.getPaymentDtoResource(payment);
    }

    /**
     * Delete a payment
     *
     * @param merchantId merchant ID of owning merchant
     * @param paymentId payment ID
     * @return ResponseEntity
     * @throws MerchantNotFoundException thrown when merchant not found by ID
     * @throws PaymentNotFoundException thrown when payment not found by ID
     */
    @DeleteMapping(value = "/{paymentId}")
    public ResponseEntity delete(@PathVariable long merchantId, @PathVariable long paymentId)
            throws MerchantNotFoundException, PaymentNotFoundException {
        //call to findOneByMerchantIdAndPaymentId ensures that the merchant and payment are linked
        if (!paymentService.isOwnerPerformingAction(
                paymentService.findOneByMerchantIdAndPaymentId(merchantId, paymentId))
                ) {
            throw new PaymentNotFoundException();
        }
        paymentService.delete(paymentId);
        return ResponseEntity.ok().build();
    }
}