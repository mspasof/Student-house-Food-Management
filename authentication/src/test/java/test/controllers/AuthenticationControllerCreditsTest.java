package test.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.authentication.Application;
import nl.tudelft.sem.authentication.entities.User;
import nl.tudelft.sem.authentication.repositories.AuthenticationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class AuthenticationControllerCreditsTest {
    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private transient HttpClient httpClient;

    @MockBean
    private transient AuthenticationRepository authenticationRepository;

    @Mock
    private transient HttpResponse<String> response;


    private final transient String forbidden = "Unauthorised access.";


    private final transient String token = "Bearer "
            + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1p"
            + "bkB0ZXN0LmNvbSIsImV4cCI6MTYxODEzNDA2NH0.PxiC074-rw8Ob"
            + "uFqjV37JySPiiBNTEzrekqCfE7B6KqA"
            + "Od8jHk6Z341fRWiGRIco1LnlyC2hM1hkwr7o7SCeTw";

    private final transient String type = "application/json";
    private final transient String headerType = "Authorization";

    private final transient String httpsGetCredits = "http://localhost:8001/getCredits";
    private final transient String httpAlterCredits = "http://localhost:8001/alterCredits";

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
    void getCreditsUnauthorizedTest() throws Exception {
        mockMvc.perform(get(httpsGetCredits))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(forbidden, result.getResponse().getContentAsString());
                });
    }

    @Test
    void getCreditsUnsuccessfulValidationTest() throws Exception {
        Mockito.when(response.statusCode()).thenReturn(400);

        mockMvc.perform(get(httpsGetCredits)
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(forbidden, result.getResponse().getContentAsString());
                });
    }

    @Test
    void getCreditsUserNotFoundTest() throws Exception {
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(authenticationRepository
                .findByEmail(user.get().getEmail())).thenReturn(Optional.empty());

        mockMvc.perform(get(httpsGetCredits)
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("A user with this id is not present in the database.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void getCreditsSuccessfulTest() throws Exception {
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body()).thenReturn(user.get().getEmail());
        Mockito.when(authenticationRepository.findByEmail(user.get().getEmail())).thenReturn(user);

        mockMvc.perform(get(httpsGetCredits)
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("Credits: " + user.get().getCredits(),
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void alterCreditsUnauthorizedTest() throws Exception {
        List<String> list = List.of("15.0", email);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(put(httpAlterCredits)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(forbidden, result.getResponse().getContentAsString());
                });
    }

    @Test
    void alterCreditsSuccessfulTest() throws Exception {
        List<String> list = List.of("15.0", email);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(user);

        mockMvc.perform(put(httpAlterCredits)
                .header(headerType, token)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("Credits updated.", result.getResponse().getContentAsString());
                });
    }

    @Test
    void alterCreditsInvalidCreditsTest() throws Exception {
        List<String> list = List.of("", email);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(user);

        mockMvc.perform(put(httpAlterCredits)
                .header(headerType, token)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("Invalid credits.", result.getResponse().getContentAsString());
                });
    }

    @Test
    void alterCreditsInvalidEmailTest() throws Exception {
        List<String> list = List.of("15.0", "not an email");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(user);

        mockMvc.perform(put(httpAlterCredits)
                .header(headerType, token)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("A user with this email does not exist.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void alterCreditsUnsuccessfulValidationTest() throws Exception {
        List<String> list = List.of("15", email);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        Mockito.when(response.statusCode()).thenReturn(400);
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(user);

        mockMvc.perform(put(httpAlterCredits)
                .header(headerType, token)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(forbidden, result.getResponse().getContentAsString());
                });
    }
}
