package rest_api.business.services.payment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.hateoas.Resource;
import rest_api.business.entities.payment.AbstractPayment;
import rest_api.business.entities.merchant.Merchant;
import rest_api.business.services.user.UserOwnedEntityServiceImpl;
import rest_api.business.services.payment.event.PaymentSaveEvent;
import rest_api.business.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rest_api.persistance.repositories.merchant.MerchantRepository;
import rest_api.persistance.repositories.payment.PaymentRepository;
import rest_api.presentation.controllers.merchant.MerchantController;
import rest_api.presentation.controllers.merchant.MerchantNotFoundException;
import rest_api.business.dtos.payment.AbstractPaymentDto;
import rest_api.presentation.controllers.payment.PaymentController;
import rest_api.presentation.controllers.payment.PaymentNotFoundException;
import rest_api.presentation.security.UnauthorizedAccessException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Implmentation of the PaymentService interface to provide interactions with the CreditCardPayment repositories
 */
@Service
public class PaymentServiceImpl extends UserOwnedEntityServiceImpl implements PaymentService {

    /**
     * Entity to DTO converter
     */
    private ModelMapper modelMapper;

    /**
     * CreditCardPayment repository
     */
    private PaymentRepository paymentRepository;

    /**
     * Merchant repository
     */
    private MerchantRepository merchantRepository;

    /**
     * Event publisher
     */
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * Setter for event publisher, optional dependency
     *
     * @param applicationEventPublisher event publisher
     */
    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Constructor
     *
     * @param userService business for interaction with User repositories
     * @param paymentRepository payment repository
     * @param merchantRepository merchant reppsitory
     */
    @Autowired
    public PaymentServiceImpl(
            UserService userService,
            ModelMapper modelMapper,
            PaymentRepository paymentRepository,
            MerchantRepository merchantRepository) {

        super(userService);
        this.modelMapper        = modelMapper;
        this.paymentRepository  = paymentRepository;
        this.merchantRepository = merchantRepository;
    }

    /**
     * Persist payment repositories
     *
     * @param merchantId ID of merchant payment is attached to
     * @param paymentDto payment repositories
     * @return CreditCardPayment
     */
    public AbstractPayment save(Long merchantId, AbstractPaymentDto paymentDto)
            throws MerchantNotFoundException, UnauthorizedAccessException {

        Merchant merchant = this.findMerchantByMerchantId(merchantId).orElseThrow(MerchantNotFoundException::new);
        AbstractPayment payment = (AbstractPayment) modelMapper.map(paymentDto, paymentDto.getEntityClass());
        payment.setOwner(getActiveUser());
        payment.setMerchant(merchant);

        payment = paymentRepository.save(payment);
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(new PaymentSaveEvent(this, payment));
        }
        return payment;
    }

    /**
     * Provide a list of all payments owned by a specific merchant
     *
     * @param merchantId ID of merchant
     * @return List<CreditCardPayment> list of payments owned by that merchant
     * @throws MerchantNotFoundException thrown if merchant by ID does not exist
     */
    public List<AbstractPayment> findAllByMerchantId(long merchantId) throws MerchantNotFoundException {
        return this.findMerchantByMerchantId(merchantId).orElseThrow(MerchantNotFoundException::new).getPayments();
    }

    /**
     * Provide a single payments owned by a specific merchant identified by payment ID
     *
     * @param merchantId ID of merchant
     * @param paymentId ID of payment
     * @return CreditCardPayment identified by both merchant and payment IDs
     * @throws MerchantNotFoundException thrown if merchant by ID does not exist
     * @throws PaymentNotFoundException thrown if payment by ID does not exist
     */
    public AbstractPayment findOneByMerchantIdAndPaymentId(long merchantId, long paymentId)
            throws MerchantNotFoundException, PaymentNotFoundException {

        Merchant merchant = this.findMerchantByMerchantId(merchantId).orElseThrow(MerchantNotFoundException::new);
        if (merchant.getPayments() == null) {
            throw new PaymentNotFoundException();
        }
        List<AbstractPayment> payments = merchant.getPayments().stream().filter(
                payment -> payment.getId() == paymentId
        ).collect(Collectors.toList());
        if (payments.size() != 1) {
            throw new PaymentNotFoundException();
        }
        return payments.get(0);
    }

    /**
     * Convert Merchant repositories to Resource<MerchantDto> repositories
     *
     * @param payment payment to be converted
     * @return Resource<AbstractPaymentDto>
     */
    public Resource<AbstractPaymentDto> getPaymentDtoResource(AbstractPayment payment) {

        return new Resource(
                modelMapper.map(payment, payment.getDtoEntityClass()),
                linkTo(PaymentController.class, payment.getMerchant().getId()).slash(payment.getId()).withSelfRel(),
                linkTo(MerchantController.class).slash(payment.getMerchant().getId()).withRel("merchant_owner")
        );

    }

    /**
     * Delete a payment
     *
     * @param id payment ID
     * @throws PaymentNotFoundException if payment by ID does not exist
     */
    public void delete(long id) throws PaymentNotFoundException {
        AbstractPayment payment =  paymentRepository.findOne(id);
        if (payment == null) {
            throw new PaymentNotFoundException();
        }
        paymentRepository.delete(payment);
    }

    /**
     * Find a merchant by it's ID
     *
     * @param merchantId merchant ID
     * @return Optional<Merchant> which contains possible null value
     */
    private Optional<Merchant> findMerchantByMerchantId(Long merchantId) {
        Merchant merchant = merchantRepository.findOne(merchantId);
        return Optional.ofNullable(merchant);
    }
}
