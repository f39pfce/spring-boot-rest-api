package rest_api.business.services.merchant;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.springframework.boot.test.context.SpringBootTest;
import rest_api.Application;
import rest_api.data_provider.MerchantProvider;
import rest_api.business.entities.merchant.Merchant;
import rest_api.persistance.repositories.merchant.MerchantRepository;
import rest_api.business.services.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.junit4.SpringRunner;
import rest_api.business.services.merchant.MerchantService;
import rest_api.presentation.controllers.merchant.MerchantDto;
import rest_api.presentation.controllers.merchant.MerchantNotFoundException;
import rest_api.presentation.security.UnauthorizedAccessException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.List;

@RunWith(SpringRunner.class)
@EnableSpringDataWebSupport
@SpringBootTest(classes = Application.class)
public class MerchantServiceImplTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MerchantService merchantService;

    @MockBean
    private MerchantRepository merchantRepository;

    @MockBean
    private Page<Merchant> page;

    @Test
    public void givenMerchantIsValid_whenSave_thenReturnMerchantId() throws UnauthorizedAccessException {
        //given
        when(merchantRepository.save(any(Merchant.class))).thenReturn(MerchantProvider.getMerchant());

        //then perform
        merchantService.save(new MerchantDto(MerchantProvider.getMerchant()));

        //and expect
        verify(merchantRepository, times(1)).save(any(Merchant.class));
    }

    @Test
    public void givenMerchantsExist_whenFindAll_thenReturnAllMerchants() {
        //given
        when(merchantRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(page.getContent()).thenReturn(MerchantProvider.getMerchants());

        //then perform
        List<Merchant> merchants = merchantService.findAll(new PageRequest(1, 1));

        //and expect
        assertThat(merchants).hasSize(3);
        for (Merchant merchant: MerchantProvider.getMerchants()) {
            assertThat(merchant.getId()).isEqualTo(merchants.get(merchant.getId().intValue() - 1).getId());
            assertThat(merchant).hasNoNullFieldsOrPropertiesExcept("createdAt", "updatedAt", "payments", "owner");
        }
        verify(merchantRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void givenNoMerchantsExist_whenFindAll_thenReturnEmptyList() {
        //given
        when(merchantRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(page.getContent()).thenReturn(MerchantProvider.getEmptyMerchants());

        List<Merchant> merchants = merchantService.findAll(new PageRequest(1, 1));

        //and expect
        assertThat(merchants).hasSize(0);
        verify(merchantRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void givenMerchantExists_whenFindOne_thenReturnMerchant() {
        //given
        long merchantId = 1L;
        when(merchantRepository.findOne(merchantId)).thenReturn(MerchantProvider.getMerchant());


        //then perform
        Merchant merchant = merchantService.findOne(merchantId);

        //and expect
        assertThat(merchant).isEqualToComparingFieldByField(MerchantProvider.getMerchant());
        assertThat(merchant).hasFieldOrPropertyWithValue("id", merchantId);
        assertThat(merchant).hasNoNullFieldsOrPropertiesExcept("createdAt", "updatedAt", "payments", "owner");
        verify(merchantRepository, times(1)).findOne(merchantId);
    }

    @Test
    public void givenMerchantExists_whenUpdateWithValidMerchant_thenReturnTrue() {
        //given
        long merchantId = 1L;
        when(merchantRepository.findOne(merchantId)).thenReturn(MerchantProvider.getMerchant());

        //then perform
        boolean updated = false;
        try {
            merchantService.update(merchantId, new MerchantDto(MerchantProvider.getAlteredMerchant()));
        } catch (Exception e) {
            fail(e.getMessage());
        }

        //and expect
        ArgumentCaptor<Merchant> argument = ArgumentCaptor.forClass(Merchant.class);
        verify(merchantRepository, times(1)).findOne(merchantId);
        verify(merchantRepository, times(1)).save(argument.capture());

        Merchant returnedMerchant = argument.getValue();
        assertThat(returnedMerchant.getId()).isEqualTo(1L);
        assertThat(returnedMerchant.getFirstName()).isEqualTo("Not-Han");
        assertThat(returnedMerchant.getLastName()).isEqualTo("Not-Solo");
        assertThat(returnedMerchant.getAddress()).isEqualTo("Not-Hoth");
        assertThat(returnedMerchant.getCity()).isEqualTo("Not-Snow Rubble City");
        assertThat(returnedMerchant.getState()).isEqualTo("Not-Of Panic");
        assertThat(returnedMerchant.getEmail()).isEqualTo("Not-rebelscum4eva@fightthepower.com");
        assertThat(returnedMerchant.getWebsite()).isEqualTo("Not-www.resist.com");
    }

    @Test(expected = ConstraintViolationException.class)
    public void givenMerchantExists_whenUpdateWithInvalidMerchant_thenReturnFalse() throws MerchantNotFoundException {
        //given
        long merchantId = 1L;
        ConstraintViolationException ex =
                new ConstraintViolationException("Invalid", new HashSet<ConstraintViolation<Merchant>>() {{}});
        when(merchantRepository.findOne(merchantId)).thenReturn(MerchantProvider.getMerchant());
        doThrow(ex).when(merchantRepository).save(any(Merchant.class));

        //then perform
        merchantService.update(merchantId, new MerchantDto(MerchantProvider.getMerchantMissingFirstName()));

        //and expect
        ArgumentCaptor<Merchant> argument = ArgumentCaptor.forClass(Merchant.class);
        verify(merchantRepository, times(1)).findOne(merchantId);
        verify(merchantRepository, times(1)).save(argument.capture());

        Merchant returnedMerchant = argument.getValue();
        assertThat(returnedMerchant.getId()).isEqualTo(1L);
        assertThat(returnedMerchant.getFirstName()).isNull();
        assertThat(returnedMerchant.getLastName()).isEqualTo("Solo");
        assertThat(returnedMerchant.getAddress()).isEqualTo("Hoth");
        assertThat(returnedMerchant.getCity()).isEqualTo("Snow Rubble City");
        assertThat(returnedMerchant.getState()).isEqualTo("Of Panic");
        assertThat(returnedMerchant.getEmail()).isEqualTo("rebelscum4eva@fightthepower.com");
        assertThat(returnedMerchant.getWebsite()).isEqualTo("www.resist.com");
    }

    @Test(expected = MerchantNotFoundException.class)
    public void givenMerchantDoesntExists_whenUpdate_thenThrowMerchantNotFoundException()
            throws MerchantNotFoundException {
        //given
        long merchantId = 1L;
        when(merchantRepository.findOne(merchantId)).thenReturn(null);

        //then perform
        merchantService.update(merchantId, new MerchantDto(MerchantProvider.getMerchantMissingFirstName()));

        //and expect
        verify(merchantRepository, times(1)).findOne(merchantId);
        verify(merchantRepository, times(0)).save(any(Merchant.class));
    }

    @Test
    public void givenMerchantExists_whenDelete_thenReturnTrue() {
        //given
        long merchantId = 1L;
        when(merchantRepository.findOne(merchantId)).thenReturn(MerchantProvider.getMerchant());

        //then perform
        try {
            merchantService.delete(1L);
        } catch (MerchantNotFoundException e) {
            fail(e.getMessage());
        }

        //and expect
        verify(merchantRepository, times(1)).findOne(merchantId);
        verify(merchantRepository, times(1)).delete(any(Merchant.class));
    }

    @Test(expected = MerchantNotFoundException.class)
    public void givenMerchantDoesntExist_whenDelete_thenThrowException() throws MerchantNotFoundException {
        //given
        long merchantId = 1L;
        when(merchantRepository.findOne(merchantId)).thenReturn(null);

        //then perform
        merchantService.delete(1L);

        //and expect
        verify(merchantRepository, times(1)).findOne(merchantId);
        verify(merchantRepository, times(0)).delete(any(Merchant.class));
    }
}
