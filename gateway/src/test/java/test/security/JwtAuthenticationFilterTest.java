package test.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import nl.tudelft.sem.gateway.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class JwtAuthenticationFilterTest {
    private static final transient String email = "email@gmail.com";
    private static final transient String password = "Password1";
    private static transient JwtAuthenticationFilter authenticationFilter;

    /**
     * The setup which is done before each test.
     */
    @BeforeEach
    public void setup() {
        AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
        Mockito.when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                email,
                password,
                new ArrayList<>()))).thenReturn(Mockito.mock(Authentication.class));
        authenticationFilter = new JwtAuthenticationFilter(authenticationManager);
    }

    @Test
    void constructorTest() {
        assertEquals("email", authenticationFilter.getUsernameParameter());
    }

    @Test
    void attemptAuthenticationTest() {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setContent(("[\"" + email + "\", \"" + password + "\"]").getBytes());
        assertNotNull(authenticationFilter.attemptAuthentication(servletRequest, null));
    }
}
