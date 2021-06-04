package nl.tudelft.sem.gateway.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JwtAuthorizationFilterUtils2 {

    /**
     * Retrieves a user's email from their token.
     *
     * @param token the token of the user
     * @return the email of the user
     */
    public static String createUserFromJwt(String token) {
        return JWT.require(Algorithm.HMAC512("TU_Delft".getBytes()))
                .build()
                .verify(token.replace("Bearer ", ""))
                .getSubject();
    }
}
