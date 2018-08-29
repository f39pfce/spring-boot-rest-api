package rest_api.business.services.merchant.event.listener;

import rest_api.business.services.merchant.event.MerchantSaveEvent;
import rest_api.business.services.merchant.boarding.FirstDataMarketplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import rest_api.config.PropertiesConfig;

/**
 * Event listener for merchant save event
 */
@Component
public class MerchantSaveEventListener implements ApplicationListener<MerchantSaveEvent> {

    /**
     * Boarding business to First Data marketplace
     */
    private FirstDataMarketplace firstDataMarketplace;

    /**
     * Access to config files
     */
    private PropertiesConfig propertiesConfig;

    /**
     * Constructor
     *
     * @param firstDataMarketplace boarding business to First Data marketplace
     * @param propertiesConfig access to config files
     */
    @Autowired
    public MerchantSaveEventListener(
            FirstDataMarketplace firstDataMarketplace,
            PropertiesConfig propertiesConfig
    ) {
        this.firstDataMarketplace = firstDataMarketplace;
        this.propertiesConfig         = propertiesConfig;
    }

    /**
     * Logic that is run on event firing
     *
     * @param event merchant save event
     */
    public void onApplicationEvent(MerchantSaveEvent event) {
        if (propertiesConfig.isFirstDataBoardOnSave()) {
            String response = firstDataMarketplace.board(event.getMerchant());
            System.out.println(response);
        }
    }
}
