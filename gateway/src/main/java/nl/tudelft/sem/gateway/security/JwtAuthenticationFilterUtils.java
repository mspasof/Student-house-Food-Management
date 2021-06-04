package nl.tudelft.sem.gateway.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationFilterUtils {

    /**
     * Maps the credentials of a user to a deque.
     *
     * @param req a HTTPServletRequest
     * @return a deque containing the credentials of a user
     * @throws RuntimeException if the body of the servlet request is not in the correct format
     */
    public static Deque<String> dequeCreator(HttpServletRequest req)
            throws AuthenticationException {
        try {
            return new ObjectMapper()
                    .readValue(req.getInputStream(), new TypeReference<LinkedList<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Attempts to authenticate a user with the given credentials
     * using the given authentication manager.
     *
     * @param email the email of the user
     * @param password the password of the user
     * @param auth the authentication manager
     * @return the Authentication of the user
     */
    public static Authentication attemptAuthenticationHelper(String email,
                                                             String password,
                                                             AuthenticationManager auth) {
        return auth.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password,
                        new ArrayList<>()));
    }
}
