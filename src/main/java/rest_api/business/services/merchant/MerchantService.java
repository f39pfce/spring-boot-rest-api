package rest_api.business.services.merchant;

import org.springframework.hateoas.Resource;
import rest_api.business.services.user.UserOwnedEntityService;
import org.springframework.data.domain.Pageable;
import rest_api.business.entities.merchant.Merchant;
import rest_api.business.dtos.merchant.MerchantDto;
import rest_api.presentation.controllers.merchant.MerchantNotFoundException;
import rest_api.presentation.security.UnauthorizedAccessException;

import java.util.List;

/**
 * Service for interactions with the Merchant Entity
 */
public interface MerchantService extends UserOwnedEntityService {

    /**
     * Convert Merchant repositories to Resource<MerchantDto> repositories
     *
     * @param merchant merchant to be converted
     * @return Resource<MerchantDto>
     */
    Resource<MerchantDto> getMerchantDtoResource(Merchant merchant);

    /**
     * Persist merchant repositories
     *
     * @param merchantDto merchant repositories transfer object
     */
    Merchant save(MerchantDto merchantDto) throws UnauthorizedAccessException;

    /**
     * Provide a paged list of Merchant repositories
     *
     * @param pageable paging properties provided by user in URL
     * @return List<Merchant>
     */
    List<Merchant> findAll(Pageable pageable);

    /**
     * Find a single merchant by their ID
     *
     * @param id merchant ID
     * @return Merchant
     */
    Merchant findOne(long id);

    /**
     * Update a merchant usng a newer state of the repositories and the existing merchant's ID
     *
     * @param id merchant ID
     * @param merchantDto merchant repositories transfer object
     * @throws MerchantNotFoundException thrown if the merchant to update is not found
     */
    Merchant update(long id, MerchantDto merchantDto) throws MerchantNotFoundException;

    /**
     * Delete an existing merchant
     *
     * @param id merchant ID
     * @throws MerchantNotFoundException thrown if the merchant to delete is not found
     */
    void delete(long id) throws MerchantNotFoundException;
}
