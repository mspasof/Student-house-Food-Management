package test.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import nl.tudelft.sem.gateway.security.JwtAuthenticationFilterUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class JwtAuthenticationFilterUtilsTest {
    private static final transient String email = "email@gmail.com";
    private static final transient String password = "Password1";

    @Test
    void dequeCreatorSuccessfulTest() {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setContent(("[\"" + email + "\", \"" + password + "\"]")
                .getBytes(StandardCharsets.UTF_8));
        Deque<String> result = new LinkedList<>();
        result.add(email);
        result.add(password);
        assertThat(JwtAuthenticationFilterUtils.dequeCreator(servletRequest)).isEqualTo(result);
    }

    @Test
    void dequeCreatorUnsuccessfulTest() {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setContent(("[\"" + email + "\", \"" + password)
                .getBytes(StandardCharsets.UTF_8));
        Deque<String> result = new LinkedList<>();
        result.add(email);
        result.add(password);
        assertThrows(Exception.class, () -> JwtAuthenticationFilterUtils
                .dequeCreator(servletRequest));
    }

    @Test
    void attemptAuthenticationHelperTest() {
        Authentication token = new UsernamePasswordAuthenticationToken(
                email, password, new ArrayList<>());
        Authentication authentication = new TestingAuthenticationToken(null, null);

        AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
        Mockito.when(authenticationManager.authenticate(token)).thenReturn(authentication);

        assertThat(JwtAuthenticationFilterUtils.attemptAuthenticationHelper(
                email, password, authenticationManager)).isEqualTo(authentication);
    }
}
