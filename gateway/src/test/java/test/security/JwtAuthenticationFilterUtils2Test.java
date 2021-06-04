package test.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Date;
import nl.tudelft.sem.gateway.security.JwtAuthenticationFilterUtils2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

public class JwtAuthenticationFilterUtils2Test {
    private static transient Authentication authentication1;
    private static transient Authentication authentication2;
    private static final transient String sign = "TU_Delft";
    private static final transient Date expiryDate =
            new Date(System.currentTimeMillis() + 864_000_000);
    private static transient String token;

    /**
     * The setup which is done before each test.
     */
    @BeforeEach
    public void setup() {
        org.springframework.security.core.userdetails.User user1 =
                Mockito.mock(org.springframework.security.core.userdetails.User.class);
        Mockito.when(user1.getUsername()).thenReturn("Username");
        authentication1 = Mockito.mock(Authentication.class);
        Mockito.when(authentication1.getPrincipal()).thenReturn(user1);

        org.springframework.security.core.userdetails.User user2 =
                Mockito.mock(org.springframework.security.core.userdetails.User.class);
        Mockito.when(user2.getUsername()).thenReturn("NotUsername");
        authentication2 = Mockito.mock(Authentication.class);
        Mockito.when(authentication2.getPrincipal()).thenReturn(user2);

        token = JwtAuthenticationFilterUtils2.createToken(authentication1, expiryDate, sign);
    }

    @Test
    void createTokenEqualSameValuesTest() {
        assertEquals(token, JwtAuthenticationFilterUtils2
                .createToken(authentication1, expiryDate, sign));
    }

    @Test
    void createTokenDifferentUserTest() {
        assertNotEquals(token, JwtAuthenticationFilterUtils2
                .createToken(authentication2, expiryDate, sign));
    }

    @Test
    void createTokenDifferentDateTest() {
        assertNotEquals(token, JwtAuthenticationFilterUtils2
                .createToken(authentication1, new Date(System.currentTimeMillis()), sign));
    }

    @Test
    void createTokenDifferentSignTest() {
        assertNotEquals(token, JwtAuthenticationFilterUtils2
                .createToken(authentication1, expiryDate, sign + "qwerty"));
    }
}
