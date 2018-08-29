package rest_api.presentation.security.hmac;

import rest_api.business.entities.user.User;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Component;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;

/**
 * Provide access to HMAC token generation encapsulated in a class to make the filter testable
 */
@Component
public class HmacUtil {

    /**
     * Generate the HMAC token
     *
     * @param user user repositories
     * @param contentToHash conotents to hash
     * @return String
     */
    public String generateHMACToken (User user, String contentToHash){
        return encodeBase64String(new HmacUtils(HmacAlgorithms.HMAC_SHA_256, user.getSecretKey()).hmac(contentToHash));
    }
}
