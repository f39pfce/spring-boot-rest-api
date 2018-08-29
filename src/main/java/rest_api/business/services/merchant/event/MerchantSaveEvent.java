package rest_api.business.services.merchant.event;

import lombok.Getter;
import rest_api.business.entities.merchant.Merchant;
import org.springframework.context.ApplicationEvent;

/**
 * Event that occurs when a new merchant is saved
 */
public class MerchantSaveEvent extends ApplicationEvent {

    /**
     * Merchant repositories
     */
    @Getter
    private Merchant merchant;

    /**
     * Constructor
     *
     * @param source object that fired the event
     * @param merchant merchant the event is about
     */
    public MerchantSaveEvent(Object source, Merchant merchant) {
        super(source);
        this.merchant = merchant;
    }
}
