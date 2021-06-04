package nl.tudelft.sem.gateway.security;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JwtAuthorizationFilterUtils {
    /**
     * Returns the JWT of a user.
     *
     * @param req a HTTPServletRequest
     * @return the JWT token of the user.
     */
    public static UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        if (token != null) {
            String user = JwtAuthorizationFilterUtils2.createUserFromJwt(token);

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }
}
