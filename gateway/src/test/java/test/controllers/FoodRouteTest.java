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
import nl.tudelft.sem.gateway.controllers.AuthenticationRoute;
import nl.tudelft.sem.gateway.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
class FoodRouteTest {

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
    void allFoodsSuccessfulTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        List<Object> foods = List.of("1", "2", "3");
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body()).thenReturn(String.valueOf(foods));

        mockMvc.perform(get("http://localhost:8000/food/all")
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("[1, 2, 3]",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void allFoodsUnauthorizedTest() throws Exception {
        mockMvc.perform(get("http://localhost:8000/food/all"))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(accessDenied, result.getResponse().getErrorMessage());
                });
    }

    @Test
    void addSuccessfulTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        List<String> list = List.of("Watermelon", "1", "2.0");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body())
                .thenReturn("Watermelon, 1, 2.0");

        mockMvc.perform(post("http://localhost:8000/food/add")
                .header(headerType, token)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("Watermelon, 1, 2.0",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void addUnauthorizedTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        List<String> list = List.of("Watermelon", "1", "2.0");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(post("http://localhost:8000/food/add")
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(accessDenied,
                            result.getResponse().getErrorMessage());
                });
    }

    @Test
    void addUnsuccessfulTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        List<String> list = List.of("Watermelon", "1", "2.0");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(post("http://localhost:8000/food/add")
                .content(body)
                .header(headerType, token)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void findByIdSuccessfulTest() throws Exception {
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body()).thenReturn("Food");
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        mockMvc.perform(get("http://localhost:8000/food/get/1")
                .header(headerType, token)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("Food",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void findByIdUnsuccessfulTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        mockMvc.perform(get("http://localhost:8000/food/get/1")
                .header(headerType, token)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void findByIdUnauthorizedTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        mockMvc.perform(get("http://localhost:8000/food/get/1")
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(accessDenied,
                            result.getResponse().getErrorMessage());
                });
    }

    @Test
    void changeNameSuccessfulTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        List<String> list = List.of("1", "Apple");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body())
                .thenReturn("{\"Success\":\"Name has been successfully changed.\"}");

        mockMvc.perform(put("http://localhost:8000/food/changeName")
                .header(headerType, token)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("{\"Success\":\"Name has been successfully changed.\"}",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void changeNameUnsuccessfulTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        List<String> list = List.of("1", "Apple");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(put("http://localhost:8000/food/changeName")
                .header(headerType, token)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                    assertEquals("",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void changeNameUnauthorizedTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        List<String> list = List.of("1", "Apple");
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        mockMvc.perform(put("http://localhost:8000/food/changeName")
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(accessDenied,
                            result.getResponse().getErrorMessage());
                });
    }

    @Test
    void deleteFoodUnauthorizedTest() throws Exception {
        mockMvc.perform(delete("http://localhost:8000/food/1/spoiled"))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(accessDenied, result.getResponse().getErrorMessage());
                });
    }

    @Test
    void deleteFoodSuccessfulTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body())
                .thenReturn("{\" Success:\":\"Food deleted successfully.\"}");

        mockMvc.perform(delete("http://localhost:8000/food/delete/1/spoiled")
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("{\" Success:\":\"Food deleted successfully.\"}",
                            result.getResponse().getContentAsString());
                });
    }

    @Test
    void resetUnauthorizedTest() throws Exception {
        mockMvc.perform(delete("http://localhost:8000/food/reset"))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(accessDenied, result.getResponse().getErrorMessage());
                });
    }

    @Test
    void resetSuccessfulTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body()).thenReturn("Food reset.");

        mockMvc.perform(delete("http://localhost:8000/food/reset")
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("Food reset.", result.getResponse().getContentAsString());
                });
    }

    @Test
    void consumeUnauthorizedTest() throws Exception {
        mockMvc.perform(post("http://localhost:8000/food/consume/1/1"))
                .andExpect(result -> {
                    assertEquals(403, result.getResponse().getStatus());
                    assertEquals(accessDenied, result.getResponse().getErrorMessage());
                });
    }

    @Test
    void consumeSuccessfulTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body()).thenReturn("Success");

        String body = "{}";

        mockMvc.perform(post("http://localhost:8000/food/consume/1/1")
                .header(headerType, token)
                .content(body)
                .contentType(type))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    assertEquals("Success", result.getResponse().getContentAsString());
                });
    }

    @Test
    void consumeUnsuccessfulTest() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        mockMvc.perform(post("http://localhost:8000/food/consume/1/1")
                .header(headerType, token))
                .andExpect(result -> {
                    assertEquals(400, result.getResponse().getStatus());
                });
    }
}