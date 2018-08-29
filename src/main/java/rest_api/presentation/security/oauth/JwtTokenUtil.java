package rest_api.presentation.security.oauth;

import java.util.Date;
import java.util.Arrays;
import java.io.Serializable;
import rest_api.config.PropertiesConfig;
import rest_api.business.entities.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utility class to assist in the management of JWT tokens
 */
@Component
public class JwtTokenUtil implements Serializable {

    /**
     * Access to config variables
     */
    private PropertiesConfig propertiesConfig;

    /**
     * Constructor
     *
     * @param propertiesConfig access to config variables
     */
    public JwtTokenUtil(PropertiesConfig propertiesConfig) {
        this.propertiesConfig = propertiesConfig;
    }

    /**
     * Extract the username from the JWT token
     *
     * @param token JWT token
     * @return String
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(propertiesConfig.getSecurity().getJwtSigningKey()).
                parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Extract the expiration date from the JWT token
     *
     * @param token JWT token
     * @return Date
     */
    private Date getExpirationDateFromToken(String token) {
        return Jwts.parser().setSigningKey(propertiesConfig.getSecurity().getJwtSigningKey()).
                parseClaimsJws(token).getBody().getExpiration();
    }

    /**
     * Determine if token is expired
     *
     * @param token JWT token
     * @return Boolean
     */
    private Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    //TODO have this return a JWT token object instead of string
    /**
     * Generate a new token
     *
     * @param user User requesting token
     * @return String
     */
    public String generateToken(User user) {
        return doGenerateToken(user.getUserName());
    }

    /**
     * Create the token
     *
     * @param username user name
     * @return String
     */
    private String doGenerateToken(String username) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("scopes", Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));

        Date issuedAt  = new Date(System.currentTimeMillis());
        Date expiresAt = new Date(
                System.currentTimeMillis() + propertiesConfig.getSecurity().getJwtTokenValidSeconds() * 1000
        );

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("http://localhost")
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(SignatureAlgorithm.HS256, propertiesConfig.getSecurity().getJwtSigningKey())
                .compact();
    }

    /**
     * Confirm the token is valid by comparing the username it stores against the userDetail user name and making
     * sure  it is also not expired
     *
     * @param token JWT token
     * @param userDetails UserDetails from security context
     * @return Boolean
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        return getUsernameFromToken(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

}