package rest_api.presentation.controllers.merchant;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import rest_api.business.entities.merchant.Merchant;
import rest_api.data_provider.MerchantProvider;
import rest_api.business.services.merchant.MerchantServiceImpl;
import rest_api.presentation.security.UnauthorizedAccessException;
import rest_api.business.entities.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.stream.Collectors;

@EnableSpringDataWebSupport
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = MerchantController.class, secure = false)
public class MerchantControllerTest {

    private static String BASE_PATH = "http://localhost/v1/merchant";
    private static long userId = 1L;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MerchantServiceImpl merchantService;

    @TestConfiguration
    static class MerchantServiceImplTestContextConfiguration {

        @Bean
        public PageRequest pageRequest() {
            return new PageRequest(1, 1);
        }
    }


    @Before
    public void setup() throws UnauthorizedAccessException {
        User user = new User();
        user.setId(userId);
        when(merchantService.getActiveUser()).thenReturn(user);
    }

    @Test
    public void givenNoMerchantsExist_whenFindAll_thenReturnEmptyJsonArray() throws Exception {
        //given that
        when(merchantService.findAll(any())).thenReturn(MerchantProvider.getEmptyMerchants());

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
        verify(merchantService, times(1)).findAll(any());
        verify(merchantService, times(1)).getActiveUser();
        verify(merchantService, times(0)).getMerchantDtoResource(any());
    }

    @Test
    public void givenMerchantsExist_whenFindAll_thenReturnOnlyOwnedMerchants() throws Exception {
        //given that
        when(merchantService.findAll(any(AbstractPageRequest.class))).thenReturn(MerchantProvider.getMerchants());
        when(merchantService.getMerchantDtoResource(any(Merchant.class))).thenCallRealMethod();

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "[{\"id\":1,\"lastName\":\"Solo\",\"firstName\":\"Han\",\"address\":\"Hoth\",\""
                        + "city\":\"Snow Rubble City\",\"state\":\"Of Panic\",\""
                        +"email\":\"rebelscum4eva@fightthepower.com\",\"website\":\"www.resist.com\","
                        + "\"links\":[{\"rel\":\"self\",\"href\":\"" + BASE_PATH + "/1\"},"
                        + "{\"rel\":\"allPayments\",\"href\":\"" + BASE_PATH + "/1/payment\"}]},"

                + "{\"id\":2,\"lastName\":\"C3\",\"firstName\":\"P0\",\"address\":\"Tatooine\",\""
                        + "city\":\"Sand Dunes\",\"state\":\"Annoyed\",\""
                        + "email\":\"primandproper@robotemailcorp.com\",\"website\":\"www.resistance.com\","
                        + "\"links\":[{\"rel\":\"self\",\"href\":\"" + BASE_PATH + "/2\"},"
                        + "{\"rel\":\"allPayments\",\"href\":\"" + BASE_PATH + "/2/payment\"}]}]"
        );
        verify(merchantService, times(1)).findAll(any(AbstractPageRequest.class));
        verify(merchantService, times(1)).getActiveUser();
        int ownedMerchants = MerchantProvider.getMerchants().stream()
                .filter(x -> x.getOwner().getId().equals(userId)).collect(Collectors.toList()).size();
        verify(merchantService, times(ownedMerchants)).getMerchantDtoResource(any(Merchant.class));
    }

    @Test
    public void givenMerchantIsSaved_thenReturnConfirmationText() throws Exception{
        //given that
        Merchant merchant = MerchantProvider.getMerchant();
        when(merchantService.getMerchantDtoResource(any(Merchant.class))).thenReturn(
                new Resource<>(
                        new MerchantDto(merchant),
                        new Link(BASE_PATH + "/1", "self"),
                        new Link(BASE_PATH + "/1/payment", "allPayments")
                )
        );

        //then perform
        MvcResult result = mvc.perform(post("/v1/merchant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\" : \"Han\", \"lastName\" : \"Solo\"," +
                        "\"address\" : \"Hoth\", \"city\" : \"Snow Rubble City\"," +
                        "\"state\" : \"Of Panic\", \"email\" : \"rebelscum4eva@fightthepower.com\"," +
                        "\"website\" : \"www.resist.com\"}"
                )
        ).andExpect(status().isOk()).andReturn();

        //and expect
        verify(merchantService, times(1)).getActiveUser();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "{\"id\":1,\"lastName\":\"Solo\",\"firstName\":\"Han\",\"address\":\"Hoth\",\""
                        + "city\":\"Snow Rubble City\",\"state\":\"Of Panic\",\""
                        +"email\":\"rebelscum4eva@fightthepower.com\",\"website\":\"www.resist.com\","
                        + "\"_links\":{\"self\":{\"href\":\"" + BASE_PATH + "/1\"},"
                        + "\"allPayments\":{\"href\":\"" + BASE_PATH + "/1/payment\"}}}"
        );
        verify(merchantService, times(1)).save(any(MerchantDto.class));
        verify(merchantService, times(1)).getMerchantDtoResource(any(Merchant.class));
    }

    @Test
    public void givenMerchantIdIsFound_thenDeleteMerchantSucceeds() throws Exception {
        //given that
        long id = 1L;
        when(merchantService.isOwnerPerformingAction(any(Merchant.class))).thenReturn(true);

        //then perform
        MvcResult result = mvc.perform(delete("/v1/merchant/" + id)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        //and expect
        verify(merchantService, times(1)).delete(id);
        verify(merchantService, times(1)).findOne(id);
        verify(merchantService, times(1)).isOwnerPerformingAction(any(Merchant.class));

    }

    @Test
    public void givenNonOwnerAction_whenDeleteAttempted_thenReturnNotFound() throws Exception {
        //given that
        long id = 1L;
        when(merchantService.isOwnerPerformingAction(any(Merchant.class))).thenReturn(false);

        //then perform
         mvc.perform(delete("/v1/merchant/" + id)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();

        //and expect
        verify(merchantService, times(0)).delete(id);
        verify(merchantService, times(1)).findOne(id);
        verify(merchantService, times(1)).isOwnerPerformingAction(any(Merchant.class));
    }

    @Test
    public void givenMerchantIdIsNotFound_thenDeleteMerchantFails() throws Exception {
        //given that
        long id = 1L;
        when(merchantService.isOwnerPerformingAction(any(Merchant.class))).thenReturn(false);
        doThrow(new MerchantNotFoundException()).when(merchantService).delete(id);

        //then perform
        mvc.perform(delete("/v1/merchant/" + id)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());

        //and expect
        verify(merchantService, times(0)).delete(id);
        verify(merchantService, times(1)).isOwnerPerformingAction(any(Merchant.class));
    }

    @Test
    public void givenNoMerchantsExist_whenFindOne_thenReturnEmptyJsonArray() throws Exception {
        //given that
        long id = 1L;
        when(merchantService.isOwnerPerformingAction(any(Merchant.class))).thenReturn(true);
        when(merchantService.findOne(id)).thenReturn(null);

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant/" + id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo("");
        verify(merchantService, times(1)).findOne(id);
        verify(merchantService, times(0)).isOwnerPerformingAction(any(Merchant.class));
        verify(merchantService, times(0)).getMerchantDtoResource(any());
    }

    @Test
    public void givenNonOwnerAction_whenFindOne_thenReturnNotFound() throws Exception {
        //given that
        long id = 1L;
        Merchant merchant = MerchantProvider.getMerchant();
        when(merchantService.findOne(id)).thenReturn(merchant);
        when(merchantService.isOwnerPerformingAction(merchant)).thenReturn(false);

        //then perform
        mvc.perform(get("/v1/merchant/" + id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();

        //and expect
        verify(merchantService, times(1)).findOne(id);
        verify(merchantService, times(1)).isOwnerPerformingAction(merchant);
        verify(merchantService, times(0)).getMerchantDtoResource(any());
    }

    @Test
    public void givenMerchantWithIdExists_whenFindOne_thenReturnMerchant() throws Exception {
        //given that
        long id = 1L;
        Merchant merchant = MerchantProvider.getMerchant();
        when(merchantService.findOne(id)).thenReturn(merchant);
        when(merchantService.isOwnerPerformingAction(merchant)).thenReturn(true);
        when(merchantService.getMerchantDtoResource(merchant)).thenReturn(
                new Resource<>(
                        new MerchantDto(merchant),
                        new Link(BASE_PATH + "/1", "self"),
                        new Link(BASE_PATH + "/1/payment", "allPayments")
                )
        );

        //then perform
        MvcResult result = mvc.perform(get("/v1/merchant/" + id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "{\"id\":1,\"lastName\":\"Solo\",\"firstName\":\"Han\",\"address\":\"Hoth\",\""
                        + "city\":\"Snow Rubble City\",\"state\":\"Of Panic\",\""
                        +"email\":\"rebelscum4eva@fightthepower.com\",\"website\":\"www.resist.com\","
                        + "\"_links\":{\"self\":{\"href\":\"" + BASE_PATH + "/1\"},"
                        + "\"allPayments\":{\"href\":\"" + BASE_PATH + "/1/payment\"}}}"
        );
        verify(merchantService, times(1)).findOne(id);
        verify(merchantService, times(1)).isOwnerPerformingAction(merchant);
        verify(merchantService, times(1)).getMerchantDtoResource(merchant);
    }

    @Test
    public void givenNoMerchantsExist_whenUpdateOnMerchant_thenReturnNotFound() throws Exception {
        //given that
        long id = 1L;
        when(merchantService.isOwnerPerformingAction(any(Merchant.class))).thenReturn(true);
        doThrow(new MerchantNotFoundException()).when(merchantService).update(eq(id), any(MerchantDto.class));

        //then perform
        mvc.perform(put("/v1/merchant/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\" : \"Han\", \"lastName\" : \"Solo\"," +
                        "\"address\" : \"Hoth\", \"city\" : \"Snow Rubble City\"," +
                        "\"state\" : \"Of Panic\", \"email\" : \"rebelscum4eva@fightthepower.com\"," +
                        "\"website\" : \"www.resist.com\"}"
                )
        ).andExpect(status().isNotFound());

        //and expect
        verify(merchantService, times(1)).isOwnerPerformingAction(any(Merchant.class));
        verify(merchantService, times(1)).update(eq(id), any(MerchantDto.class));
        verify(merchantService, times(0)).getMerchantDtoResource(any());
    }

    @Test
    public void givenMerchantsExist_whenUpdateOnMerchant_thenReturnSuccess() throws Exception {
        //given that
        long id = 1L;
        when(merchantService.isOwnerPerformingAction(any(Merchant.class))).thenReturn(true);
        when(merchantService.getMerchantDtoResource(any(Merchant.class))).thenReturn(
                new Resource<>(
                        new MerchantDto(MerchantProvider.getMerchant()),
                        new Link(BASE_PATH + "/1", "self"),
                        new Link(BASE_PATH + "/1/payment", "allPayments")
                )
        );

        //then perform
        MvcResult result = mvc.perform(put("/v1/merchant/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\" : \"Han\", \"lastName\" : \"Solo\"," +
                        "\"address\" : \"Hoth\", \"city\" : \"Snow Rubble City\"," +
                        "\"state\" : \"Of Panic\", \"email\" : \"rebelscum4eva@fightthepower.com\"," +
                        "\"website\" : \"www.resist.com\"}"
                )
        ).andExpect(status().isOk()).andReturn();

        //and expect
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
                "{\"id\":1,\"lastName\":\"Solo\",\"firstName\":\"Han\",\"address\":\"Hoth\",\""
                        + "city\":\"Snow Rubble City\",\"state\":\"Of Panic\",\""
                        +"email\":\"rebelscum4eva@fightthepower.com\",\"website\":\"www.resist.com\","
                        + "\"_links\":{\"self\":{\"href\":\"" + BASE_PATH + "/1\"},"
                        + "\"allPayments\":{\"href\":\"" + BASE_PATH + "/1/payment\"}}}"
        );
        verify(merchantService, times(1)).update(eq(id), any(MerchantDto.class));
        verify(merchantService, times(1)).isOwnerPerformingAction(any(Merchant.class));
        verify(merchantService, times(1)).getMerchantDtoResource(any(Merchant.class));
    }

    @Test
    public void givenNonOwnerAction_whenUpdateOnMerchant_thenReturnNotFound() throws Exception {
        //given that
        long id = 1L;
        when(merchantService.isOwnerPerformingAction(any(Merchant.class))).thenReturn(false);

        //then perform
        mvc.perform(put("/v1/merchant/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\" : \"Han\", \"lastName\" : \"Solo\"," +
                        "\"address\" : \"Hoth\", \"city\" : \"Snow Rubble City\"," +
                        "\"state\" : \"Of Panic\", \"email\" : \"rebelscum4eva@fightthepower.com\"," +
                        "\"website\" : \"www.resist.com\"}"
                )
        ).andExpect(status().isNotFound()).andReturn();

        //and expect
        verify(merchantService, times(0)).update(eq(id), any(MerchantDto.class));
        verify(merchantService, times(1)).isOwnerPerformingAction(any(Merchant.class));
        verify(merchantService, times(0)).getMerchantDtoResource(any());
    }

    @Test
    public void givenMissingFieldsInUpdate_whenUpdateAttempted_returnBadRequest() throws Exception {
        //given that
        long id = 1L;
        ConstraintViolationException ex =
                new ConstraintViolationException("Invalid", new HashSet<ConstraintViolation<Merchant>>() {{}});
        when(merchantService.isOwnerPerformingAction(any(Merchant.class))).thenReturn(true);
        doThrow(ex).when(merchantService).update(eq(id), any(MerchantDto.class));

        //then perform
        mvc.perform(put("/v1/merchant/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"lastName\" : \"Solo\"," +
                        "\"address\" : \"Hoth\", \"city\" : \"Snow Rubble City\"," +
                        "\"state\" : \"Of Panic\", \"email\" : \"rebelscum4eva@fightthepower.com\"," +
                        "\"website\" : \"www.resist.com\"}"
                )
        ).andExpect(status().isBadRequest()).andReturn();

        //and expect
        verify(merchantService, times(0)).isOwnerPerformingAction(any(Merchant.class));
        verify(merchantService, times(0)).update(eq(id), any(MerchantDto.class));
        verify(merchantService, times(0)).getMerchantDtoResource(any());
    }
}
