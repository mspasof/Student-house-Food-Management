package test.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Objects;
import nl.tudelft.sem.gateway.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

public class SecurityConfigTest {
    private static final transient SecurityConfig securityConfig = new SecurityConfig(null, null);
    private static transient CorsConfigurationSource result;

    @BeforeEach
    public void setup() {
        result = securityConfig.corsConfigurationSource();
    }

    @Test
    void configurationNotNull() {
        assertNotNull(result);

    }

    @Test
    void configurationCorrect() {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        assertEquals(new CorsConfiguration().applyPermitDefaultValues().getAllowCredentials(),
                Objects.requireNonNull(result.getCorsConfiguration(servletRequest))
                        .getAllowCredentials());
    }
}
