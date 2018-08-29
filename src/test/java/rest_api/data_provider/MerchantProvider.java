package rest_api.data_provider;

import java.lang.reflect.Field;
import java.util.*;

import rest_api.business.entities.merchant.Merchant;
import rest_api.business.entities.payment.AbstractPayment;
import rest_api.business.entities.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.fail;

public class MerchantProvider {

    public static List<Merchant> getEmptyMerchants() {
        return Collections.emptyList();
    }

    public static List<Merchant> getMerchants() {
        ObjectMapper mapper = new ObjectMapper();

        User owner1 = new User();
        owner1.setId(1L);
        User owner2 = new User();
        owner2.setId(2L);

        ArrayList<Map<String, String>> merchants = new ArrayList<>();

        Map<String, String> merchant1 = new HashMap<>();
        merchant1.put("id", "1");
        merchant1.put("lastName", "Solo");
        merchant1.put("firstName", "Han");
        merchant1.put("address", "Hoth");
        merchant1.put("city", "Snow Rubble City");
        merchant1.put("state", "Of Panic");
        merchant1.put("email", "rebelscum4eva@fightthepower.com");
        merchant1.put("website", "www.resist.com");
        merchants.add(merchant1);

        Map<String, String> merchant2 = new HashMap<>();
        merchant2.put("id", "2");
        merchant2.put("lastName", "C3");
        merchant2.put("firstName", "P0");
        merchant2.put("address", "Tatooine");
        merchant2.put("city", "Sand Dunes");
        merchant2.put("state", "Annoyed");
        merchant2.put("email", "primandproper@robotemailcorp.com");
        merchant2.put("website", "www.resistance.com");
        merchants.add(merchant2);

        Map<String, String> merchant3 = new HashMap<>();
        merchant3.put("id", "3");
        merchant3.put("lastName", "R2");
        merchant3.put("firstName", "D2");
        merchant3.put("address", "Tatooine");
        merchant3.put("city", "Scrap Yard");
        merchant3.put("state", "Always pleasant");
        merchant3.put("email", "beepboopbeep@beepboopbeep.com");
        merchant3.put("website", "www.beepboopbeep.com");
        merchants.add(merchant3);

        List<Merchant> list = new ArrayList<>();
        for (Map<String, String> merchant : merchants) {
            Merchant merchantObject = mapper.convertValue(merchant, Merchant.class);
            merchantObject.setOwner(merchantObject.getId().equals(3L) ? owner2 : owner1);
            list.add(merchantObject);
        }
        return list;
    }

    public static Merchant getMerchant() {
        return MerchantProvider.getConstructedMerchant(false, true);
    }

    public static Merchant getAlteredMerchant() {
        return MerchantProvider.getConstructedMerchant(true, true);
    }

    public static Merchant getMerchantMissingFirstName() {
        return MerchantProvider.getConstructedMerchant(false, false);
    }

    private static Merchant getConstructedMerchant(boolean altered, boolean assignFirstName) {
        ObjectMapper mapper = new ObjectMapper();

        String prefix =  altered ? "Not-" : "";

        Map<String, String> merchant = new HashMap<>();
        merchant.put("id", "1");
        if (assignFirstName) {
            merchant.put("firstName", prefix + "Han");
        }
        merchant.put("lastName",  prefix + "Solo");
        merchant.put("address",   prefix + "Hoth");
        merchant.put("city",      prefix + "Snow Rubble City");
        merchant.put("state",     prefix + "Of Panic");
        merchant.put("email",     prefix + "rebelscum4eva@fightthepower.com");
        merchant.put("website",   prefix + "www.resist.com");

        return mapper.convertValue(merchant, Merchant.class);
    }

    public static Merchant attachPaymentsToMerchant(Merchant merchant) {

        List<AbstractPayment> paymentList = PaymentProvider.getPayments();
        for (AbstractPayment payment : paymentList) {
            payment.setMerchant(merchant);
        }
        try {
            Field payments = Class.forName("rest_api.business.entities.merchant.Merchant").getDeclaredField("payments");
            payments.setAccessible(true);
            payments.set(merchant, paymentList);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to reflect the merchant class.");
        }
        return merchant;
    }
}
