package test.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import nl.tudelft.sem.foodmanagement.Application;
import nl.tudelft.sem.foodmanagement.controllers.FoodController;
import nl.tudelft.sem.foodmanagement.entities.Food;
import nl.tudelft.sem.foodmanagement.repositories.FoodRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@ContextConfiguration(classes = Application.class)
class FoodControllerTest {
    private static Food food1;

    private final transient int success = HttpStatus.OK.value();
    private final transient int unauthorised = HttpStatus.FORBIDDEN.value();
    private final transient String type = "application/json";
    private final transient String forbidden = "Unauthorised access.";
    private final transient String headerType = "Authorization";
    private final transient String longName = "aaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private final transient String longName255 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private final transient String addRoute = "http://localhost:8002/add";
    private final transient String deleteRoute = "http://localhost:8002/delete/";
    private final transient String changeNameRoute = "http://localhost:8002/changeName";
    private final transient String email = "admin@test.com";
    private final transient String dummyEmail = "[\"email\"]";
    private final transient String error = "Error.";

    private transient String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1p"
            + "bkB0ZXN0LmNvbSIsImV4cCI6MTYxODEzNDA2NH0.PxiC074-rw8ObuFqjV37JySPiiBNTEzrekqCfE7B6KqA"
            + "Od8jHk6Z341fRWiGRIco1LnlyC2hM1hkwr7o7SCeTw";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private transient HttpClient httpClient;

    @Mock
    private transient HttpResponse<String> mockResponse;

    @Mock
    private transient HttpResponse<String> mockResponse2;

    @Mock
    private transient HttpResponse<String> mockResponse3;

    @MockBean
    private transient FoodRepository foodRepository;

    @InjectMocks
    private transient FoodController foodController;

    @BeforeEach
    public void initialize() throws IOException, InterruptedException {
        when(httpClient.send(Mockito.any(),
                eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResponse)
                .thenReturn(mockResponse2)
                .thenReturn(mockResponse3);
        food1 = new Food("milk", 5, 2.0f);
    }

    @Test
    void controllerLoads() {
        assertThat(foodController).isNotNull();
    }

    @Test
    void testAll() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        List<Food> list = List.of(food1);
        when(foodRepository.findAll()).thenReturn(list);
        mockMvc.perform(get("http://localhost:8002/all")
                .header(headerType, token))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.OK.value());
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("[" + food1.getId() + "]");
                });
    }

    @Test void testAllUnauthorised() throws Exception {
        mockMvc.perform(get("http://localhost:8002/all")
                .header(headerType, token))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(unauthorised);
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(forbidden);
                });
    }

    @Test
    void testAddFood() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        when(mockResponse2.statusCode()).thenReturn(HttpStatus.OK.value());
        List<String> list = List.of(food1.getName(),
                String.valueOf(food1.getPortions()),
                String.valueOf(food1.getPrice()));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        when(foodRepository.save(food1)).thenReturn(food1);
        when(foodRepository.findById(food1.getId()))
                .thenReturn(java.util.Optional.of(food1));

        mockMvc.perform(post(addRoute)
                .header(headerType, token)
                .content(body)
                .contentType(type)).andExpect(
                result -> {
                    assertThat(result.getResponse().getStatus() == HttpStatus.OK.value());
                    assertThat(result.getResponse().getContentAsString()).isEqualTo(
                            "{\"id\":" + food1.getId() + ",\"name\":\""
                                    + food1.getName() + "\",\"price\":"
                                    + food1.getPrice() / food1.getPortions()
                                    + ",\"portions\":" + food1.getPortions() + "}"
                    );
                }
        );
    }


    @Test
    void testAddFoodWithNoName() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        List<String> list = List.of("",
                String.valueOf(food1.getPortions()),
                String.valueOf(food1.getPrice()));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        mockMvc.perform(post(addRoute)
                .header(headerType, token)
                .content(body)
                .contentType(type)).andExpect(
                result -> {
                    assertThat(result.getResponse()
                            .getStatus() == HttpStatus.BAD_REQUEST.value());
                    assertThat(result.getResponse()
                            .getContentAsString()).isEqualTo("Name field must not be empty.");
                }
        );
    }

    @Test
    void testAddFoodWithTooLongName() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        String name = longName.concat(longName).concat(longName).concat(longName);
        List<String> list = List.of(name,
                String.valueOf(food1.getPortions()),
                String.valueOf(food1.getPrice()));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        mockMvc.perform(post(addRoute)
                .header(headerType, token)
                .content(body)
                .contentType(type)).andExpect(
                result -> {
                    assertThat(result.getResponse()
                            .getStatus() == HttpStatus.BAD_REQUEST.value());
                    assertThat(result.getResponse()
                            .getContentAsString())
                            .isEqualTo("Name field must be less than 255 characters long.");
                }
        );
    }

    @Test
    void testAddFoodWithTooLongName255() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        String name = longName255;
        List<String> list = List.of(name,
                String.valueOf(food1.getPortions()),
                String.valueOf(food1.getPrice()));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        mockMvc.perform(post(addRoute)
                .header(headerType, token)
                .content(body)
                .contentType(type)).andExpect(
                result -> {
                    assertThat(result.getResponse()
                            .getStatus() == HttpStatus.BAD_REQUEST.value());
                    assertThat(result.getResponse()
                            .getContentAsString())
                            .isEqualTo("Name field must be less than 255 characters long.");
                }
        );
    }

    @Test
    void testAddFoodWithNonPositivePortions() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        List<String> list = List.of(food1.getName(),
                String.valueOf(0),
                String.valueOf(food1.getPrice()));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        mockMvc.perform(post(addRoute)
                .header(headerType, token)
                .content(body)
                .contentType(type)).andExpect(
                result -> {
                    assertThat(result.getResponse()
                            .getStatus() == HttpStatus.BAD_REQUEST.value());
                    assertThat(result.getResponse()
                            .getContentAsString())
                            .isEqualTo("Number of portions should be a positive integer.");
                }
        );
    }

    @Test
    void testAddFoodWithNegativePrice() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        List<String> list = List.of(food1.getName(),
                String.valueOf(food1.getPortions()),
                String.valueOf(-1));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        mockMvc.perform(post(addRoute)
                .header(headerType, token)
                .content(body)
                .contentType(type)).andExpect(
                result -> {
                    assertThat(result.getResponse()
                            .getStatus() == HttpStatus.BAD_REQUEST.value());
                    assertThat(result.getResponse()
                            .getContentAsString())
                            .isEqualTo("Price should be a non-negative number.");
                }
        );
    }

    @Test
    void testAddFoodWithWrongParams() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        String portions = "a";
        List<String> list = List.of(food1.getName(), portions, String.valueOf(food1.getPrice()));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        mockMvc.perform(post(addRoute)
                .header(headerType, token)
                .content(body)
                .contentType(type)).andExpect(
                result -> {
                    assertThat(result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Incompatible parameters passed to the request.");
                }
        );
    }

    @Test
    void testAddFoodUnauthorised() throws Exception {
        when(mockResponse.statusCode()).thenReturn(unauthorised);
        List<String> list = List.of(food1.getName(),
                String.valueOf(food1.getPortions()),
                String.valueOf(food1.getPrice()));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);
        mockMvc.perform(post(addRoute)
                .header(headerType, token)
                .content(body)
                .contentType(type)).andExpect(
                result -> {
                    assertThat(result.getResponse().getStatus() == unauthorised);
                    assertThat(result.getResponse().getContentAsString()).isEqualTo(forbidden);
                }
        );
    }

    @Test
    void testDeleteFoodNotSpoiledNoCharge() throws Exception {
        when(foodRepository.existsById(food1.getId())).thenReturn(Boolean.TRUE);
        when(mockResponse.body()).thenReturn(dummyEmail);
        doNothing().when(foodRepository).delete(food1);
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        mockMvc.perform(delete(deleteRoute
                + food1.getId() + "/" + "noCharge")
                .header(headerType, token)
                .contentType(type))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(success);
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Food deleted successfully.");
                });
        Mockito.verify(foodRepository, times(1)).deleteById(food1.getId());
    }


    @Test
    void testDeleteFoodNotSpoiledWithCharge() throws Exception {
        when(foodRepository.existsById(food1.getId())).thenReturn(Boolean.TRUE);
        when(mockResponse.body()).thenReturn(dummyEmail);
        when(foodRepository.findById(food1.getId()))
                .thenReturn(java.util.Optional.ofNullable(food1));
        doNothing().when(foodRepository).delete(food1);
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse2.statusCode()).thenReturn(HttpStatus.OK.value());
        mockMvc.perform(delete(deleteRoute
                + food1.getId() + "/" + "charge")
                .header(headerType, token)
                .contentType(type))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(success);
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Food deleted successfully.");
                    System.out.println(mockResponse2.body());
                });
        Mockito.verify(foodRepository, times(1)).deleteById(food1.getId());
    }

    @Test
    void testDeleteFoodSpoiled() throws Exception {
        when(foodRepository.existsById(food1.getId())).thenReturn(Boolean.TRUE);
        when(foodRepository.findById(food1.getId()))
                .thenReturn(java.util.Optional.ofNullable(food1));
        doNothing().when(foodRepository).delete(food1);
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse2.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse3.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        when(mockResponse2.body()).thenReturn(dummyEmail);
        mockMvc.perform(delete(deleteRoute
                + food1.getId() + "/" + "spoiled")
                .header(headerType, token)
                .contentType(type))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(success);
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Food deleted successfully.");
                });
        Mockito.verify(foodRepository, times(1)).deleteById(food1.getId());
    }


    @Test
    void testDeleteFoodUnauthorised() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.FORBIDDEN.value());
        mockMvc.perform(delete(deleteRoute
                + food1.getId() + "/" + "noCharge")
                .header(headerType, token)
                .contentType(type))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(unauthorised);
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(forbidden);
                });
    }

    @Test
    void testDeleteFoodNotPresent() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        mockMvc.perform(delete(deleteRoute
                + food1.getId() + "/" + "spoiled")
                .header(headerType, token)
                .contentType(type))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Food with the given id does not exist in the database.");
                });
    }

    @Test
    void testReset() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        mockMvc.perform(delete("http://localhost:8002/reset")
                .header(headerType, token))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(success);
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Foods deleted successfully.");
                });
    }

    @Test
    void testResetUnauthorised() throws Exception {
        when(mockResponse.statusCode()).thenReturn(unauthorised);
        mockMvc.perform(delete("http://localhost:8002/reset")
                .header(headerType, token))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(unauthorised);
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(forbidden);
                });
    }


    @Test
    void testFindByCorrectId() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        when(foodRepository.findById(food1.getId())).thenReturn(java.util.Optional.of(food1));
        mockMvc.perform(get("http://localhost:8002/get/" + food1.getId())
                .header(headerType, token)
                .contentType(type))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(success);
                    assertThat(result.getResponse().getContentAsString()).isEqualTo(
                            "{\"id\":" + food1.getId() + ",\"name\":\""
                                    + food1.getName() + "\",\"price\":"
                                    + food1.getPrice() + ",\"portions\":"
                                    + food1.getPortions() + "}");

                });
    }

    @Test
    void testFindByIncorrectId() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(foodRepository.findById(0L)).thenReturn(java.util.Optional.empty());
        when(mockResponse.body()).thenReturn(dummyEmail);
        mockMvc.perform(get("http://localhost:8002/get/" + 0L)
                .header(headerType, token)
                .contentType(type))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("No food with such id.");

                });
    }

    @Test
    void testFindByIdUnauthorised() throws Exception {
        when(mockResponse.statusCode()).thenReturn(unauthorised);
        mockMvc.perform(get("http://localhost:8002/get/" + 0L)
                .header(headerType, token)
                .contentType(type))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(unauthorised);
                    assertThat(result.getResponse().getContentAsString()).isEqualTo(forbidden);

                });
    }

    @Test
    void testChangeNameSuccessfully() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        when(foodRepository.findById(food1.getId()))
                .thenReturn(java.util.Optional.ofNullable(food1));
        mockMvc.perform(put(changeNameRoute)
                .header(headerType, token)
                .contentType(type)
                .content("[0, \"banitsa\"]"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(success);
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Name updated.");
                });
    }

    @Test
    void testChangeNameUnauthorised() throws Exception {
        when(mockResponse.statusCode()).thenReturn(unauthorised);
        mockMvc.perform(put(changeNameRoute)
                .header(headerType, token)
                .contentType(type)
                .content("[0, \"banitsa\"]"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(unauthorised);
                    assertThat(result.getResponse().getContentAsString()).isEqualTo(forbidden);
                });
    }

    @Test
    void testChangeNameTooLong() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        when(foodRepository.findById(food1.getId())).thenReturn(java.util.Optional.of(food1));
        doNothing().when(foodRepository).delete(food1);

        String name = longName.concat(longName).concat(longName).concat(longName);

        mockMvc.perform(put(changeNameRoute)
                .header(headerType, token)
                .contentType(type)
                .content("[0, \"" + name + "\"]"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Name field must be less than 255 characters long.");
                });
    }

    @Test
    void testChangeNameNotPresent() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);

        mockMvc.perform(put(changeNameRoute)
                .header(headerType, token)
                .contentType(type)
                .content("[0, \"banitsa\"]"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.NOT_FOUND.value());
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Food does not exist.");
                });
    }

    @Test
    void testConsumeUnauthorised() throws Exception {
        mockMvc.perform(post("http://localhost:8002/consume/1/4")
                .contentType(type)
                .content("{}"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(unauthorised);
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(forbidden);
                });
    }

    @Test
    void testConsumeUnsuccessful() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse2.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(email);
        when(mockResponse2.body()).thenReturn(email);
        when(foodRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(food1));
        mockMvc.perform(post("http://localhost:8002/consume/1/2")
                .header(headerType, token)
                .contentType(type)
                .content("{\"admind@test.com\":1}"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(error);
                });
    }

    @Test
    void testConsumeUnsuccessful2() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(email);
        when(foodRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(food1));
        mockMvc.perform(post("http://localhost:8002/consume/1/6")
                .header(headerType, token)
                .contentType(type)
                .content("{}"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(error);
                });
    }

    @Test
    void testConsumeUnsuccessful3() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(email);
        when(foodRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(food1));
        mockMvc.perform(post("http://localhost:8002/consume/1/3")
                .header(headerType, token)
                .contentType(type)
                .content("{\"" + email + "\":1}"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(error);
                });
    }

    @Test
    void testConsumeUnsuccessful4() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(email);
        when(mockResponse2.statusCode()).thenReturn(200);
        when(mockResponse2.body()).thenReturn("[\"admin@test.com\"]");
        when(foodRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(food1));
        when(mockResponse3.statusCode()).thenReturn(400);
        mockMvc.perform(post("http://localhost:8002/consume/1/1")
                .header(headerType, token)
                .contentType(type)
                .content("{}"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(error);
                });
    }

    @Test
    void testConsumeSuccessful() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(email);
        when(mockResponse2.statusCode()).thenReturn(200);
        when(mockResponse2.body()).thenReturn("[\"admin@test.com\"]");
        when(foodRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(food1));
        when(mockResponse3.statusCode()).thenReturn(200);
        mockMvc.perform(post("http://localhost:8002/consume/1/5")
                .header(headerType, token)
                .contentType(type)
                .content("{}"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(200);
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Food consumed.");
                });
    }

    @Test
    void testConsumeSuccessful2() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(email);
        when(mockResponse2.statusCode()).thenReturn(200);
        when(mockResponse2.body()).thenReturn("[\"" + email + "\"]");
        when(foodRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(food1));
        when(mockResponse3.statusCode()).thenReturn(200);
        mockMvc.perform(post("http://localhost:8002/consume/1/3")
                .header(headerType, token)
                .contentType(type)
                .content("{}"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(200);
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Food portions consumed.");
                });
    }

    @Test
    void testConsumeUnsuccessful5() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        when(mockResponse2.statusCode()).thenReturn(200);
        when(mockResponse2.body()).thenReturn("");
        when(foodRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(food1));
        mockMvc.perform(post("http://localhost:8002/consume/1/3")
                .header(headerType, token)
                .contentType(type)
                .content("{}"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(error);
                });
    }

    @Test
    void testConsumeUnsuccessful6() throws Exception {
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(mockResponse.body()).thenReturn(dummyEmail);
        when(foodRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(food1));
        mockMvc.perform(post("http://localhost:8002/consume/0/3")
                .header(headerType, token)
                .contentType(type)
                .content("{}"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.NOT_FOUND.value());
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Food does not exist.");
                });
    }

}