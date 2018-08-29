package rest_api.presentation.controllers.merchant;

import javax.validation.Valid;
import java.util.stream.Collectors;
import org.springframework.hateoas.Resource;
import rest_api.business.dtos.merchant.MerchantDto;
import rest_api.business.dtos.merchant.MerchantDtoResources;
import rest_api.business.entities.merchant.Merchant;
import rest_api.business.services.merchant.MerchantService;
import rest_api.presentation.security.UnauthorizedAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for performing actions using Merchant repositories
 */
@RestController
@RequestMapping("/v1/merchant")
public class MerchantController {

    /**
     * Service for Merchant repositories interactions
     */
    private MerchantService merchantService;

    /**
     * Setter for merchant business
     *
     * @param merchantService business for Merchant repositories interactions
     */
    @Autowired
    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    /**
     * Action for the creation of merchant repositories
     *
     * @param merchantDto merchant to be created
     * @return Resource<MerchantDto> Return a Resource of the Merchant and its Links
     * @throws UnauthorizedAccessException thrown on any unauthorized access
     */
    @PostMapping
    public Resource<MerchantDto> create(@Valid @RequestBody MerchantDto merchantDto)
            throws UnauthorizedAccessException {

        Merchant merchant = merchantService.save(merchantDto);
        return merchantService.getMerchantDtoResource(merchant);
    }

    /**
     * Action returns a list of all merchants owned by the user
     *
     * @param pageable user supplied size and page parameters from URL
     * @return MerchantDtoResources XML serializable wrapper for List<Resource<MerchantDto>>
     * @throws UnauthorizedAccessException thrown on any unauthorized access
     */
    @GetMapping
    public MerchantDtoResources find(Pageable pageable) throws UnauthorizedAccessException {
        long id = merchantService.getActiveUser().getId();
        return new MerchantDtoResources(merchantService.findAll(pageable).stream()
                .filter(x -> x.getOwner().getId().equals(id))
                .map(merchantService::getMerchantDtoResource).collect(Collectors.toList()));
    }

    /**
     * Action to update fields of merchant repositories
     *
     * @param id Long that represents the merchant's ID
     * @param merchantDto the merchant repositories to be updated
     * @return Resource<MerchantDto> Return a Resource of the Merchant and its Links
     * @throws MerchantNotFoundException thrown when merchant not found
     */
    @PutMapping(value= "/{id}")
    public Resource<MerchantDto> update(@PathVariable long id, @Valid @RequestBody MerchantDto merchantDto)
            throws MerchantNotFoundException {
        if (!merchantService.isOwnerPerformingAction(merchantService.findOne(id))) {
            throw new MerchantNotFoundException();
        }
        Merchant merchant = merchantService.update(id, merchantDto);
        return merchantService.getMerchantDtoResource(merchant);
    }

    /**
     * Action to delete a merchant
     *
     * @param id Long that represents the merchant's ID
     * @return ResponseEntity that represents the HTTP response
     * @throws MerchantNotFoundException thrown when merchant not found
     */
    @DeleteMapping(value= "/{id}")
    public ResponseEntity delete(@PathVariable long id) throws MerchantNotFoundException {
        if (!merchantService.isOwnerPerformingAction(merchantService.findOne(id))) {
            throw new MerchantNotFoundException();
        }
        merchantService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Action to find an individual merchant by their id
     *
     * @param id Long that represents the merchant's ID
     * @return Resource<MerchantDto> Return a Resource of the Merchant and its Links
     * @throws MerchantNotFoundException thrown when merchant not found
     */
    @GetMapping(value= "/{id}", produces={"application/json","application/xml"})
    public Resource<MerchantDto> find(@PathVariable long id) throws MerchantNotFoundException {
        Merchant merchant = merchantService.findOne(id);
        if (merchant == null || !merchantService.isOwnerPerformingAction(merchant)) {
            throw new MerchantNotFoundException();
        }
        return merchantService.getMerchantDtoResource(merchant);
    }
}
