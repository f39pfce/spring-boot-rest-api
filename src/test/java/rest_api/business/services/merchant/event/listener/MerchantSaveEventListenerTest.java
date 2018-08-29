package rest_api.business.services.merchant.event.listener;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import rest_api.Application;
import rest_api.business.services.merchant.event.MerchantSaveEvent;
import rest_api.business.services.merchant.boarding.FirstDataMarketplace;
import rest_api.config.PropertiesConfig;
import rest_api.business.entities.merchant.Merchant;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class MerchantSaveEventListenerTest {

    private static Merchant merchant;
    private static MerchantSaveEvent merchantSaveEvent;

    @Autowired
    MerchantSaveEventListener merchantSaveEventListener;

    @MockBean
    FirstDataMarketplace firstDataMarketplace;

    @Autowired
    PropertiesConfig propertiesConfig;

    @BeforeClass
    public static void setUp() {
        merchant = new Merchant();
        merchantSaveEvent = new MerchantSaveEvent(Merchant.class, merchant);
    }

    @Test
    public void givenBoardingIsOn_whenSaveMerchant_thenBoard() {
        //given that
        propertiesConfig.setFirstDataBoardOnSave(true);

        //then perform
        merchantSaveEventListener.onApplicationEvent(merchantSaveEvent);

        //and expect
        verify(firstDataMarketplace, times(1)).board(merchant);
    }

    @Test
    public void givenBoardingIsOff_whenSaveMerchant_thenDoNotBoard() {
        //given that
        propertiesConfig.setFirstDataBoardOnSave(false);

        //then perform
        merchantSaveEventListener.onApplicationEvent(merchantSaveEvent);

        //and expect
        verify(firstDataMarketplace, times(0)).board(merchant);
    }
}
