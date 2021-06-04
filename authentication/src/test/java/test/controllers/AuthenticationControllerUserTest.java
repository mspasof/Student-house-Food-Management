package test.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.authentication.Application;
import nl.tudelft.sem.authentication.controllers.AuthenticationController;
import nl.tudelft.sem.authentication.entities.User;
import nl.tudelft.sem.authentication.repositories.AuthenticationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@ActiveProfiles("test")
@ContextConfiguration(classes = Application.class)
public class AuthenticationControllerUserTest {

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private transient HttpClient httpClient;

    @MockBean
    private transient AuthenticationRepository authenticationRepository;

    @Mock
    private transient HttpResponse<String> response;


    @InjectMocks
    private transient AuthenticationController authenticationController;

    private final transient String forbidden = "Unauthorised access.";
    private final transient String secretHeader = "e91e6348157868de9dd8b25c81aebfb9";
    private final transient  String secretValue = "0ad210d6fa2d8f4f2775dd31f7a0ca0c";

    private final transient String token = "Bearer "
            + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1p"
            + "bkB0ZXN0LmNvbSIsImV4cCI6MTYxODEzNDA2NH0.PxiC074-rw8Ob"
            + "uFqjV37JySPiiBNTEzrekqCfE7B6KqA"
            + "Od8jHk6Z341fRWiGRIco1LnlyC2hM1hkwr7o7SCeTw";

    private final transient String headerType = "Authorization";
    private final transient String httpPassword = "http://localhost:8001/password/admin@test.com";
    private final transient String httpDelete = "http://localhost:8001/delete";


    private final transient String email = "admin@test.com";
    private final transient String password = "Password1";
    private final transient String firstName = "Admin";
    private final transient String lastName = "DoNotTouch";
    private final transient Optional<User> user = Optional.of(new User(email, password,
            firstName, lastName, 0));
    private final transient User admin = new User(email, password, firstName, lastName, 0);

    @BeforeEach
    public void initialize() throws IOException, InterruptedException {
        Mockito.when(httpClient.send(Mockito.any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(response);
    }


    @Test
    void authenticateUserTest() throws Exception {
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(user);

        mockMvc.perform(get(httpPassword)
                .header(secretHeader, secretValue))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals(user.get().getPassword(),
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void authenticateUserUnauthorizedTest() throws Exception {
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(user);

        mockMvc.perform(get(httpPassword))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("Unauthorized access.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void authenticateUserUnauthorizedTest2() throws Exception {
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(user);

        String temp = "0ad210d6fa2d8f4f2775dd31f7a0ca0a";
        mockMvc.perform(get(httpPassword)
                .header(secretHeader, temp))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("Unauthorized access.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void authenticateUserNotFoundTest() throws Exception {
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(Optional.empty());

        mockMvc.perform(get(httpPassword)
                .header(secretHeader, secretValue))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("A user with this username does not exist.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void getAllUsersUnauthorizedTest() throws Exception {
        mockMvc.perform(get("http://localhost:8001/all"))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(forbidden, result.getResponse().getContentAsString());
                });
    }

    @Test
    void getAllUserSuccessfulTest() throws Exception {
        List<User> list = List.of(admin);
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body()).thenReturn(email);
        Mockito.when(authenticationRepository.findAll()).thenReturn(list);

        mockMvc.perform(get("http://localhost:8001/all")
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("[\"admin@test.com\"]",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void deleteUserUnauthorizedTest() throws Exception {
        mockMvc.perform(delete(httpDelete))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(forbidden, result.getResponse().getContentAsString());
                });
    }

    @Test
    void deleteUserUnsuccessfulValidationTest() throws Exception {
        Mockito.when(response.statusCode()).thenReturn(400);
        
        mockMvc.perform(delete(httpDelete)
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(forbidden, result.getResponse().getContentAsString());
                });
    }

    @Test
    void deleteUserSuccessfulTest() throws Exception {
        Mockito.when(response.statusCode()).thenReturn(200);
        
        mockMvc.perform(delete(httpDelete)
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("{\" Success:\":\"User deleted successfully.\"}",
                            result.getResponse().getContentAsString());
                });
    }


    @Test
    void resetUnauthorizedTest() throws Exception {
        mockMvc.perform(delete("http://localhost:8001/reset"))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(forbidden, result.getResponse().getContentAsString());
                });
    }

    @Test
    void resetSuccessfulTest() throws Exception {
        List<User> list = List.of(admin);
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(authenticationRepository.findAll()).thenReturn(list);
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(user);

        mockMvc.perform(delete("http://localhost:8001/reset")
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("Credits reset.", result.getResponse().getContentAsString());
                });
    }

    @Test
    void resetUnsuccessfulValidationTest() throws Exception {
        List<User> list = List.of(admin);
        Mockito.when(response.statusCode()).thenReturn(400);
        Mockito.when(authenticationRepository.findAll()).thenReturn(list);
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(user);

        mockMvc.perform(delete("http://localhost:8001/reset")
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(forbidden, result.getResponse().getContentAsString());
                });
    }
}
