package test.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
public class AuthenticationControllerRegistrationTest {
    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private transient HttpClient httpClient;

    @MockBean
    private transient AuthenticationRepository authenticationRepository;

    @Mock
    private transient HttpResponse<String> response;

    private final transient String type = "application/json";
    private final transient String httpRegister = "http://localhost:8001/register";

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
    void registerUserEmailAlreadyTakenTest() throws Exception {
        List<String> list = List.of(email, password, firstName, lastName);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(user);

        mockMvc.perform(post(httpRegister)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("This email has already been taken.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerUserEmailTooLongTest() throws Exception {
        List<String> list = List.of("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                        + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                        + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                        + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                        + "aaaaaaaaaaaaa@test.com",
                password, firstName, lastName);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(user);

        mockMvc.perform(post(httpRegister)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("The email is too long.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerUserInvalidEmailTest() throws Exception {
        List<String> list = List.of("qwerty", password, firstName, lastName);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(user);

        mockMvc.perform(post(httpRegister)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("Invalid email.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerUserPasswordTooShortTest() throws Exception {
        List<String> list = List.of(email, "Qwerty1", firstName, lastName);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(post(httpRegister)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("The password must be at least 8 characters long.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerUserPasswordTooLongTest() throws Exception {
        List<String> list = List.of(email, "Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa1", firstName, lastName);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(post(httpRegister)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("The password must be at most 30 characters long.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerUserPasswordContainsEmptySpacesTest() throws Exception {
        List<String> list = List.of(email, "Qwer ty12", firstName, lastName);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(post(httpRegister)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("The password must not contain empty spaces or new lines.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerUserPasswordContainsNewLineTest() throws Exception {
        List<String> list = List.of(email, "Qwer\nty12", firstName, lastName);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(post(httpRegister)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("The password must not contain empty spaces or new lines.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerUserInvalidPasswordTest() throws Exception {
        List<String> list = List.of(email, "Qwertyuiop", firstName, lastName);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(post(httpRegister)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("The password must contain at least "
                                    + "one uppercase letter, one lowercase letter and one digit.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerUserFirstNameTooLongTest() throws Exception {
        List<String> list = List.of(email, password, "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
                + "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
                + "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
                + "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb", lastName);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(post(httpRegister)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("The first name must be at most 255 characters long.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerUserInvalidFirstNameTest() throws Exception {
        List<String> list = List.of(email, password, "aaaaa", lastName);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(post(httpRegister)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("The first name must start with a capital letter"
                                    + " and only contain lowercase letters afterwards.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerUserLastNameTooLongTest() throws Exception {
        List<String> list = List.of(email, password, firstName, "cccccccccccccccccccccccccccccccccc"
                + "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"
                + "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"
                + "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(post(httpRegister)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("The last name must be at most 255 characters long.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerUserInvalidLastNameTest() throws Exception {
        List<String> list = List.of(email, password, firstName, "aaaaa  aaaaaaaaa %#@");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(post(httpRegister)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("The last name must only contain letters and empty spaces.",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerUnsuccessfulHashTest() throws Exception {
        List<String> list = List.of(email, password, firstName, lastName);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(response.statusCode()).thenReturn(400);
        

        mockMvc.perform(post(httpRegister).content(body).contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("{ \"error\": \"Communication with gateway failed.\" }",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void registerSuccessfulTest() throws Exception {
        List<String> list = List.of(email, password, firstName, lastName);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        Mockito.when(authenticationRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body()).thenReturn("encoded password");
        

        mockMvc.perform(post(httpRegister).content(body).contentType(type))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals(mapper.writeValueAsString(user.get()),
                            result.getResponse().getContentAsString());
                });
    }
}
