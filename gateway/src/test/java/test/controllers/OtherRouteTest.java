package test.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import nl.tudelft.sem.gateway.Application;
import nl.tudelft.sem.gateway.controllers.AuthenticationRoute;
import nl.tudelft.sem.gateway.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@ContextConfiguration(classes = Application.class)
public class OtherRouteTest {
    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private transient HttpClient httpClient;

    @Mock
    private transient HttpResponse<String> response;

    @Mock
    private transient HttpResponse<String> response2;

    @MockBean
    private transient UserDetailsServiceImpl userDetailsService;

    private transient List<String> list = List.of("email@email.com", "password", "Jack", "Jackson");

    private transient String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1p"
            + "bkB0ZXN0LmNvbSIsImV4cCI6MTYxODEzNDA2NH0.PxiC074-rw8ObuFqjV37JySPiiBNTEzrekqCfE7B6KqA"
            + "Od8jHk6Z341fRWiGRIco1LnlyC2hM1hkwr7o7SCeTw";

    private transient String type = "application/json";
    private transient String headerType = "Authorization";
    private transient String accessDenied = "Access Denied";
    private final transient String secretHeader = "e91e6348157868de9dd8b25c81aebfb9";
    private final transient  String secretValue = "0ad210d6fa2d8f4f2775dd31f7a0ca0c";

    @Test
    void testHashSuccessful() throws Exception {
        String string = "Secure112233";

        mockMvc.perform(post("http://localhost:8000/hash")
                .content("[\"" + string + "\"]")
                .header(headerType, token)
                .header(secretHeader, secretValue))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertNotNull(result.getResponse().getContentAsString());
                });
    }

    @Test
    void testHashUnsuccessful() throws Exception {
        String string = "Secure112233";
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        mockMvc.perform(post("http://localhost:8000/hash")
                .content("[\"" + string + "\"]")
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("Unauthorized access.",
                            result.getResponse().getContentAsString());
                });
    }


    @Test
    void testValidateSuccessful() throws Exception {

        mockMvc.perform(get("http://localhost:8000/validate")
                .header(headerType, token)
                .header(secretHeader, secretValue))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("admin@test.com", result.getResponse().getContentAsString());
                });
    }

    @Test
    void testValidateUnsuccessful() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        mockMvc.perform(get("http://localhost:8000/validate")
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("Unauthorized access.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void testResetSuccessful() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        Mockito.when(response.statusCode()).thenReturn(200);
        mockMvc.perform(delete("http://localhost:8000/reset")
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("Credits reset and foods deleted.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void testResetUnsuccessful() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        Mockito.when(response.statusCode()).thenReturn(500);
        mockMvc.perform(delete("http://localhost:8000/reset")
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("Communication with server failed.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void testResetUnsuccessful2() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(response).thenReturn(response2);
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response2.statusCode()).thenReturn(500);
        mockMvc.perform(delete("http://localhost:8000/reset")
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("Communication with server failed.",
                            result.getResponse().getContentAsString());
                });
    }
}
