package rest_api.business.services.merchant;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.hateoas.Resource;
import rest_api.business.services.user.UserOwnedEntityServiceImpl;
import rest_api.business.entities.merchant.Merchant;
import rest_api.business.services.merchant.event.MerchantSaveEvent;
import rest_api.presentation.controllers.payment.PaymentController;
import rest_api.business.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rest_api.persistance.repositories.merchant.MerchantRepository;
import rest_api.presentation.controllers.merchant.MerchantController;
import rest_api.business.dtos.merchant.MerchantDto;
import rest_api.presentation.controllers.merchant.MerchantNotFoundException;
import rest_api.presentation.security.UnauthorizedAccessException;

import javax.validation.constraints.NotNull;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Implmentation of the MerchantService interface to provide interactions with the Merchant repositories
 */
@Service
public class MerchantServiceImpl extends UserOwnedEntityServiceImpl implements MerchantService {

    /**
     * Entity to DTO converter
     */
    private ModelMapper modelMapper;

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
     * @param merchantRepository merchant repository
     * @param userService business to interact with User repositories
     */
    @Autowired
    public MerchantServiceImpl(
            MerchantRepository merchantRepository,
            UserService userService,
            ModelMapper modelMapper) {
        super(userService);
        this.merchantRepository = merchantRepository;
        this.modelMapper        = modelMapper;
    }

    /**
     * Persist merchant repositories
     *
     * @param merchantDto merchant repositories transfer object
     */
    public Merchant save(MerchantDto merchantDto) throws UnauthorizedAccessException {
        Merchant merchant = modelMapper.map(merchantDto, Merchant.class);
        merchant.setOwner(this.getActiveUser());

        merchant = merchantRepository.save(merchant);
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(new MerchantSaveEvent(this, merchant));
        }
        return merchant;
    }

    /**
     * Provide a paged sub-list of Merchant repositories
     *
     * @param pageable paging properties provided by user in URL
     * @return List<Merchant>
     */
    public List<Merchant> findAll(Pageable pageable) {
        return merchantRepository.findAll(pageable).getContent();
    }

    /**
     * Find a single merchant by their ID
     *
     * @param id merchant ID
     * @return Merchant
     */
    public Merchant findOne(long id) {
        return merchantRepository.findOne(id);
    }

    /**
     * Update a merchant usng a newer state of the repositories and the existing merchant's ID
     *
     * @param id merchant ID
     * @param merchantDto newer state of merchant repositories to update to
     * @throws MerchantNotFoundException thrown if the merchant to update is not found
     */
    public Merchant update(long id, MerchantDto merchantDto) throws MerchantNotFoundException {
        Merchant existingMerchant = merchantRepository.findOne(id);
        if (existingMerchant == null) {
            throw new MerchantNotFoundException();
        }
        existingMerchant.setFirstName(merchantDto.getFirstName());
        existingMerchant.setLastName(merchantDto.getLastName());
        existingMerchant.setAddress(merchantDto.getAddress());
        existingMerchant.setCity(merchantDto.getCity());
        existingMerchant.setState(merchantDto.getState());
        existingMerchant.setEmail(merchantDto.getEmail());
        existingMerchant.setWebsite(merchantDto.getWebsite());

        return merchantRepository.save(existingMerchant);
    }

    /**
     * Delete an existing merchant
     *
     * @param id merchant ID
     * @throws MerchantNotFoundException thrown if the merchant to delete is not found
     */
    public void delete(long id) throws MerchantNotFoundException {
        Merchant merchant =  merchantRepository.findOne(id);
        if (merchant == null) {
            throw new MerchantNotFoundException();
        }
        merchantRepository.delete(merchant);
    }

    /**
     * Convert Merchant repositories to Resource<MerchantDto> repositories
     *
     * @param merchant merchant to be converted
     * @return Resource<MerchantDto>
     */
    @NotNull
    public Resource<MerchantDto> getMerchantDtoResource(Merchant merchant) {
        return new Resource<>(
                new MerchantDto(merchant),
                linkTo(MerchantController.class).slash(merchant.getId()).withSelfRel(),
                linkTo(PaymentController.class, merchant.getId()).withRel("allPayments")
        );
    }
}