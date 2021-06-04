package nl.tudelft.sem.gateway.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import com.auth0.jwt.JWT;
import java.util.Date;
import org.springframework.security.core.Authentication;

public class JwtAuthenticationFilterUtils2 {

    /**
     * Create the JWT of a user.
     *
     * @param auth the Authentication of the user
     * @param expiryDate the date when the token expires
     * @param sign a custom string used for the uniqueness of the token
     * @return the JWT of the user
     */
    public static String createToken(Authentication auth, Date expiryDate, String sign) {
        return JWT.create()
                .withSubject(((org.springframework.security.core.userdetails.User)
                        auth.getPrincipal()).getUsername())
                .withExpiresAt(expiryDate)
                .sign(HMAC512(sign.getBytes()));
    }
}
