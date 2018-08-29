package rest_api.business.services.merchant.boarding;

import static org.apache.commons.codec.binary.Base64.*;

import java.util.Date;
import java.util.TimeZone;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import rest_api.config.PropertiesConfig;
import rest_api.business.entities.merchant.Merchant;
import java.text.SimpleDateFormat;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.springframework.stereotype.Service;

/**
 * Service to board merchants to the First Data Marketplace
 */
@Service
public class FirstDataMarketplace {

    /**
     * Access to config varibles
     */
    private PropertiesConfig propertiesConfig;

    /**
     * Constructor
     *
     * @param propertiesConfig access to config variables
     */
    @Autowired
    public FirstDataMarketplace(PropertiesConfig propertiesConfig) {
        this.propertiesConfig = propertiesConfig;
    }

    /**
     * Board a merchant to first repositories marketplace
     *
     * @param merchant Merchant repositories
     * @return String
     */
    public String board(Merchant merchant) {
        try {
            StringEntity payload = new StringEntity(this.buildRequestString(merchant));
            HttpResponse response = doPost("/marketplace/v1/merchantorders", payload);
            JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
            return "json:" + json.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Create a post request
     *
     * @param uri URI to post to
     * @param payload contents of post
     * @return HttpResponse
     * @throws Exception on error
     */
    private HttpResponse doPost(String uri, StringEntity payload) throws Exception {
        String formattedDate = this.getCurrentGMTDate();
        String authHeader = "hmac username=\"" + propertiesConfig.getFirstDataBoarding().getUser() +
                "\", algorithm=\"hmac-sha1\", headers=\"date\"" +
                ", signature=\"" + this.generateHMACToken(formattedDate) + "\"";
        Header oauthHeader = new BasicHeader("authorization", authHeader );
        Header dateHeader = new BasicHeader("date", formattedDate);

        HttpClient httpClientLead = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(propertiesConfig.getFirstDataBoarding().getUrl() + uri);
        httpPost.addHeader(oauthHeader);
        httpPost.addHeader(dateHeader);
        httpPost.addHeader("Content-Type","application/json");
        httpPost.setEntity(payload);
        return httpClientLead.execute(httpPost);
    }

    /**
     * Get formatted date per Marketplace specifications
     *
     * @return String
     */
    private String getCurrentGMTDate(){
        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date());
    }

    /**
     * Generate First Data Marketplace HMAC token
     *
     * @param formattedDate formatted date
     * @return String
     */
    private String generateHMACToken (String formattedDate){
        return encodeBase64String(
                new HmacUtils(HmacAlgorithms.HMAC_SHA_1, propertiesConfig.getFirstDataBoarding().getSecretKey())
                        .hmac("date: "+ formattedDate)
        );
    }

    //TODO currently stubbed
    /**
     * Builds the request string
     *
     * @param merchant Merchant repositories
     * @return String
     */
    private String buildRequestString(Merchant merchant) {
        return "{\n" +
                "  \"company\": \"John's Business Supplies\",\n" +
                "  \"numberofLocations\": 1,\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Smith\",\n" +
                "  \"email\": \"johnsmith@emai.com\",\n" +
                "  \"phone\": \"1234567890\",\n" +
                "  \"address1\": \"123 Main Street\",\n" +
                "  \"address2\": \"Suite 123\",\n" +
                "  \"city\": \"SCHENECTADY\",\n" +
                "  \"state\": \"NY\",\n" +
                "  \"postalCode\": \"12345\",\n" +
                "  \"recordType\": \"Lead\",\n" +
                "  \"cardNotPresent\": 1,\n" +
                "  \"pricingDetails\": ["+
                "    {"+
                "      \"productId\": \"67702\",\n" +
                "      \"quantity\": 1,\n" +
                "      \"productName\": \"Clover Station W/ Cash Drawer\",\n" +
                "      \"feeMinAbsolute\": 0,\n" +
                "      \"feeMin\": 0,\n" +
                "      \"feeDefault\": 0,\n" +
                "      \"feeMax\": 0,\n" +
                "      \"feeMaxAbsolute\": 0,\n" +
                "      \"minAmountAbsolute\": 499.00,\n" +
                "      \"minAmt\": 999.00,\n" +
                "      \"defaultAmt\": 1599.00,\n" +
                "      \"maxAmt\": 1840.00,\n" +
                "      \"maxAmountAbsolute\": 1840.00,\n" +
                "      \"rateMinAbsolute\": 0,\n" +
                "      \"rateMin\": 0,\n" +
                "      \"rateDefault\": 0,\n" +
                "      \"rateMax\": 0,\n" +
                "      \"rateMaxAbsolute\": 0,\n" +
                "      \"productType\": \"IBUNDLE\",\n" +
                "      \"isOverride\": false,\n" +
                "      \"override\": false,\n" +
                "      \"showoncart\": false,\n" +
                "      \"purchaseType\": \"P\",\n" +
                "      \"occurrence\": {"+
                "        \"type\": \"Onetime_Product\""+
                "      },\n" +
                "      \"paymentType\": \"P\",\n" +
                "      \"paymentTerm\": \"\",\n" +
                "      \"category\": \"RETAIL\""+
                "    },\n" +
                "    {"+
                "      \"productId\": \"59462\",\n" +
                "      \"quantity\": 1,\n" +
                "      \"productName\": \"Transarmor Monthly Fee\",\n" +
                "      \"description\": \"4TA_TA_MOFEE\",\n" +
                "      \"feeMinAbsolute\": 0,\n" +
                "      \"feeMin\": 0,\n" +
                "      \"feeDefault\": 0,\n" +
                "      \"feeMax\": 0,\n" +
                "      \"feeMaxAbsolute\": 0,\n" +
                "      \"minAmountAbsolute\": 0,\n" +
                "      \"minAmt\": 0,\n" +
                "      \"defaultAmt\": 0,\n" +
                "      \"maxAmt\": 0,\n" +
                "      \"maxAmountAbsolute\": 0,\n" +
                "      \"rateMinAbsolute\": 0,\n" +
                "      \"rateMin\": 0,\n" +
                "      \"rateDefault\": 0,\n" +
                "      \"rateMax\": 0,\n" +
                "      \"rateMaxAbsolute\": 0,\n" +
                "      \"productType\": \"MO_FEE\",\n" +
                "      \"isOverride\": false,\n" +
                "      \"override\": false,\n" +
                "      \"showoncart\": true,\n" +
                "      \"occurrence\": {"+
                "        \"type\": \"Recurring\""+
                "      },\n" +
                "      \"disclosure\": \"Per Location\",\n" +
                "      \"productAttribute\": {"+
                "        \"name\": \"SOLUTION_FEE\",\n" +
                "        \"value\": \"Clover security Plus\",\n" +
                "        \"domain\": \"PRICING\""+
                "      }"+
                "    },\n" +
                "    {"+
                "      \"productId\": \"3\",\n" +
                "      \"quantity\": 1,\n" +
                "      \"productName\": \"MasterCard Qualified Credit\",\n" +
                "      \"description\": \"MC\",\n" +
                "      \"feeMinAbsolute\": 0,\n" +
                "      \"feeMin\": 0,\n" +
                "      \"feeDefault\": 0,\n" +
                "      \"feeMax\": 0,\n" +
                "      \"feeMaxAbsolute\": 0,\n" +
                "      \"minAmountAbsolute\": 0,\n" +
                "      \"minAmt\": 0,\n" +
                "      \"defaultAmt\": 0.29,\n" +
                "      \"maxAmt\": 10,\n" +
                "      \"maxAmountAbsolute\": 10,\n" +
                "      \"rateMinAbsolute\": 0,\n" +
                "      \"rateMin\": 0,\n" +
                "      \"rateDefault\": 0.109,\n" +
                "      \"rateMax\": 2.309,\n" +
                "      \"rateMaxAbsolute\": 5,\n" +
                "      \"productType\": \"NET_FEE\",\n" +
                "      \"isOverride\": false,\n" +
                "      \"override\": false,\n" +
                "      \"showoncart\": false,\n" +
                "      \"occurrence\": {"+
                "        \"type\": \"Transaction\""+
                "      },\n" +
                "      \"groupName\": \"Qualified Credit\",\n" +
                "      \"cardPresntFlag\": 0,\n" +
                "      \"cardNotPresent\": 0,\n" +
                "      \"parentOrder\": 1"+
                "    }"+
                "  ],\n" +
                "  \"pricingOptions\": {"+
                "    \"companyId\": 386,\n" +
                "    \"transactionInfo\": {"+
                "      \"mccTypes\": \"Appliances, Electronics, Computers\",\n" +
                "      \"mcc\": \"5734\",\n" +
                "      \"annualVolume\": 200000,\n" +
                "      \"creditCardVolume\": 150000,\n" +
                "      \"averageTicket\": 20,\n" +
                "      \"highestTicket\": 300,\n" +
                "      \"category\": \"RETAIL\""+
                "    }"+
                "  },\n" +
                "  \"shippingAddress\": ["+
                "    {"+
                "      \"company_name\": \"John's Business Supplies\",\n" +
                "      \"firstName\": \"John\",\n" +
                "      \"lastName\": \"Smith\",\n" +
                "      \"address1\": \"123 Main Street\",\n" +
                "      \"address2\": \"Suite 123\",\n" +
                "      \"city\": \"SCHENECTADY\",\n" +
                "      \"state\": \"NY\",\n" +
                "      \"postalCode\": \"12345\",\n" +
                "      \"email\": \"johnsmith@email.com\",\n" +
                "      \"email2\": \"johnsmith@email.com\",\n" +
                "      \"phone\": \"1234567890\",\n" +
                "      \"productstoShip\": ["+
                "        {"+
                "          \"productId\": \"67702\",\n" +
                "          \"term\": \"P\""+
                "        }"+
                "      ]"+
                "    }"+
                "  ],\n" +
                "  \"cartDetails\": {"+
                "    \"repositories\": ["+
                "      {"+
                "        \"productId\": \"67702\",\n" +
                "        \"name\": \"Clover Station W/ Cash Drawer\",\n" +
                "        \"price\": 1599,\n" +
                "        \"term\": \"P\",\n" +
                "        \"qty\": 1,\n" +
                "        \"category\": \"RETAIL\",\n" +
                "        \"productType\": \"Terminal\""+
                "      },\n" +
                "      {"+
                "        \"productId\": 50712,\n" +
                "        \"name\": \"Gnd\",\n" +
                "        \"price\": 19.95,\n" +
                "        \"term\": \"P\",\n" +
                "        \"qty\": 1,\n" +
                "        \"category\": \"RETAIL\",\n" +
                "        \"productType\": \"SHIPPING\""+
                "      },\n" +
                "      {"+
                "        \"productId\": \"10013\",\n" +
                "        \"name\": \"Visa/MasterCard\",\n" +
                "        \"price\": 0,\n" +
                "        \"qty\": 1,\n" +
                "        \"category\": \"RETAIL\",\n" +
                "        \"productType\": \"ACQUIRING\""+
                "      },\n" +
                "      {"+
                "        \"productId\": \"10017\",\n" +
                "        \"name\": \"Discover\",\n" +
                "        \"price\": 0,\n" +
                "        \"qty\": 1,\n" +
                "        \"category\": \"RETAIL\",\n" +
                "        \"productType\": \"ACQUIRING\""+
                "      },\n" +
                "      {"+
                "        \"productId\": \"10021\",\n" +
                "        \"name\": \"American Express\",\n" +
                "        \"price\": 0,\n" +
                "        \"qty\": 1,\n" +
                "        \"category\": \"RETAIL\",\n" +
                "        \"productType\": \"ACQUIRING\""+
                "      },\n" +
                "      {"+
                "        \"productId\": \"10023\",\n" +
                "        \"name\": \"PayPal\",\n" +
                "        \"price\": 0,\n" +
                "        \"qty\": 1,\n" +
                "        \"category\": \"RETAIL\",\n" +
                "        \"productType\": \"ACQUIRING\""+
                "      }"+
                "    ],\n" +
                "    \"amount\": 1599.00,\n" +
                "    \"shipping_amount\": 19.95,\n" +
                "    \"tax\": 111.93,\n" +
                "    \"taxPercent\": 0.07,\n" +
                "    \"total\": 1730.88,\n" +
                "    \"shipping_option_id\": 1,\n" +
                "    \"purchaseEnabled\": true,\n" +
                "    \"total_qty\": 1"+
                "  }"+
                "}";

    }
}
