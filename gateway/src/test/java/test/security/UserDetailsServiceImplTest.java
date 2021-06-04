package test.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import nl.tudelft.sem.gateway.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;


public class UserDetailsServiceImplTest {
    private static final transient String email = "email@gmail.com";
    private static final transient String password = "Password1";
    private static final transient String errorMessage = "\"Communication with server failed.\"";
    private static transient HttpClient httpClient;
    private static transient HttpResponse<String> httpResponse;
    private static transient UserDetailsServiceImpl userDetailsService;

    /**
     * The setup which is done before each test.
     */
    @BeforeEach
    public void setup() {
        httpClient = Mockito.mock(HttpClient.class);
        httpResponse = Mockito.mock(HttpResponse.class);
        userDetailsService = new UserDetailsServiceImpl(httpClient);
    }

    @Test
    void emptyConstructorTest() {
        UserDetailsServiceImpl test = new UserDetailsServiceImpl();
        assertNotNull(test);
    }

    @Test
    void constructorTest() {
        assertNotNull(userDetailsService);
    }

    @Test
    void loadUserByUsernameUnauthorizedTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                    eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);
        Mockito.when(httpResponse.statusCode()).thenReturn(HttpStatus.UNAUTHORIZED.value());
        assertThrows(RuntimeException.class, () -> userDetailsService.loadUserByUsername(email));
    }

    @Test
    void loadUserByUsernameSuccessfulTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                    eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);
        Mockito.when(httpResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        Mockito.when(httpResponse.body()).thenReturn(password);
        assertNotNull(userDetailsService.loadUserByUsername(email));
    }

    @Test
    void loadUserByUsernameCommunicationFailedTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                    eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new RuntimeException(errorMessage));
        Exception exception = assertThrows(RuntimeException.class,
                () -> userDetailsService.loadUserByUsername(email));
        assertEquals(errorMessage, exception.getMessage());
    }
}
