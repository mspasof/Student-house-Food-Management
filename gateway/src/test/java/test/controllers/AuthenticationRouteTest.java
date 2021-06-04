package test.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import nl.tudelft.sem.gateway.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class AuthenticationRouteTest {

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private transient BCryptPasswordEncoder encoder;

    @MockBean
    private transient HttpClient httpClient;

    @MockBean
    private transient HttpResponse<String> response;

    @MockBean
    private transient UserDetailsServiceImpl userDetailsService;

    private transient List<String> list = List.of("email@email.com", "password", "Jack", "Jackson");

    private transient String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1p"
            + "bkB0ZXN0LmNvbSIsImV4cCI6MTYxODEzNDA2NH0.PxiC074-rw8ObuFqjV37JySPiiBNTEzrekqCfE7B6KqA"
            + "Od8jHk6Z341fRWiGRIco1LnlyC2hM1hkwr7o7SCeTw";
    
    private transient String type = "application/json";
    private transient String headerType = "Authorization";
    private transient String accessDenied = "Access Denied";

    @Test
    void allUsersSuccessfulTest() throws Exception {
        List<Object> users = List.of("Pesho", "Gosho", "Qnko");
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body()).thenReturn(String.valueOf(users));
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        mockMvc.perform(get("http://localhost:8000/user/all").header(headerType, token))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("[Pesho, Gosho, Qnko]",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void allUsersUnauthorizedTest() throws Exception {
        mockMvc.perform(get("http://localhost:8000/user/all"))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(accessDenied, result.getResponse().getErrorMessage());
                });
    }

    @Test
    void registerSuccessfulTest() throws Exception {
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body())
                .thenReturn("maseto@pusha.com, qkaparola, masur, masurski");
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        List<String> list = List.of("maseto@pusha.com", "qkaparola", "masur", "masurski");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(post("http://localhost:8000/user/register").content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("maseto@pusha.com, qkaparola, masur, masurski",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerUnsuccessfulTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        List<String> list = List.of("admin@test.com", "password", "admin", "DoNotTouch");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(post("http://localhost:8000/user/register").content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void changePasswordSuccessfulTest() throws Exception {
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body())
                .thenReturn("{\"Success\":\"Password has been successfully changed.\"}");
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        List<String> list = List.of("newPassword");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(put("http://localhost:8000/user/changePassword").header(headerType, token)
                .content(body).contentType(type))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("{\"Success\":\"Password has been successfully changed.\"}",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void changePasswordUnauthorizedTest() throws Exception {
        List<String> list = List.of("newPassword");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(put("http://localhost:8000/user/changePassword")
                .content(body).contentType(type))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(accessDenied, result.getResponse().getErrorMessage());
                });
    }

    @Test
    void deleteUnauthorizedTest() throws Exception {
        mockMvc.perform(delete("http://localhost:8000/user/delete"))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(accessDenied, result.getResponse().getErrorMessage());
                });
    }

    @Test
    void deleteSuccessfulTest() throws Exception {
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body())
                .thenReturn("{\" Success:\":\"User deleted successfully.\"}");
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        mockMvc.perform(delete("http://localhost:8000/user/delete").header(headerType, token))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("{\" Success:\":\"User deleted successfully.\"}",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void getCreditsUnauthorizedTest() throws Exception {
        mockMvc.perform(get("http://localhost:8000/user/getCredits"))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(accessDenied, result.getResponse().getErrorMessage());
                });
    }

    @Test
    void getCreditsSuccessfulTest() throws Exception {
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body())
                .thenReturn("Credits: 0.0");
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        mockMvc.perform(get("http://localhost:8000/user/getCredits").header(headerType, token))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("Credits: 0.0",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void alterCreditsSuccessfulTest() throws Exception {
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body()).thenReturn("Credits updated.");
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        List<String> list = List.of("15.0");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(put("http://localhost:8000/user/alterCredits").header(headerType, token)
                .content(body).contentType(type))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("Credits updated.", result.getResponse().getContentAsString());
                });
    }

    @Test
    void alterCreditsUnauthorizedTest() throws Exception {
        List<String> list = List.of("15.0");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(put("http://localhost:8000/user/alterCredits")
                .content(body).contentType(type))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(accessDenied, result.getResponse().getErrorMessage());
                });
    }

    @Test
    void resetUnauthorizedTest() throws Exception {
        mockMvc.perform(delete("http://localhost:8000/user/reset"))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(accessDenied, result.getResponse().getErrorMessage());
                });
    }

    @Test
    void resetSuccessfulTest() throws Exception {
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body()).thenReturn("Credits reset.");
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        mockMvc.perform(delete("http://localhost:8000/user/reset")
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("Credits reset.", result.getResponse().getContentAsString());
                });
    }
}
