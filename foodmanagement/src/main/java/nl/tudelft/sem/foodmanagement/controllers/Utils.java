package nl.tudelft.sem.foodmanagement.controllers;

import static nl.tudelft.sem.foodmanagement.controllers.FoodController.sendRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.foodmanagement.controllers.strategies.ChargeStrategy;
import nl.tudelft.sem.foodmanagement.controllers.strategies.NoChargeStrategy;
import nl.tudelft.sem.foodmanagement.controllers.strategies.SpoiledStrategy;
import nl.tudelft.sem.foodmanagement.entities.Food;
import nl.tudelft.sem.foodmanagement.validators.InvalidFoodException;
import nl.tudelft.sem.foodmanagement.validators.NameValidator;
import nl.tudelft.sem.foodmanagement.validators.PortionsValidator;
import nl.tudelft.sem.foodmanagement.validators.PriceValidator;
import nl.tudelft.sem.foodmanagement.validators.Validator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Utils {

    private static final String type = "Content-Type";
    private static final String value = "application/json";
    private static final transient String authHeader = "Authorization";
    private static final transient ResponseEntity.BodyBuilder badRequest
            = ResponseEntity.badRequest();
    private static final transient ResponseEntity.BodyBuilder ok = ResponseEntity.ok();
    private static final transient HttpRequest.Builder builder = HttpRequest.newBuilder();

    /**
     * Initializes a deleter object based on a given strategy.
     *
     * @param deletionMethod The strategy we want to follow.
     * @return A Deleter with the correct strategy.
     */
    public static Deleter instantiateDeleter(String deletionMethod) {
        switch (deletionMethod) {
            case "spoiled":
                return new Deleter(new SpoiledStrategy());
            case "charge":
                return new Deleter(new ChargeStrategy());
            case "noCharge":
                return new Deleter(new NoChargeStrategy());
            default:
                return null;
        }
    }

    /**
     * Checks whether the total portions people want to consume is possible.
     *
     * @param food The food we want to consume
     * @param others The people mapped to their portions
     * @param totalPortions Portions we want to consume
     * @return Whether the user gave valid amount of portions to be consumed.
     */
    public static boolean checkPortions(Food food, Map<String, Integer> others, int totalPortions) {
        return !(totalPortions > food.getPortions()
                || others
                .entrySet()
                .stream()
                .mapToInt(entry -> entry.getValue())
                .max()
                .getAsInt() > food.getPortions()
                || others
                .entrySet()
                .stream()
                .mapToInt(entry -> entry.getValue())
                .min()
                .getAsInt() < 1);
    }

    /**
     * Validates a request based on a JWT Security token.
     *
     * @param request The request made to the server
     * @param httpClient The client we use for communication with the gateway
     * @return User's email if the token is correct, otherwise null
     */
    public static String authenticate(HttpServletRequest request, HttpClient httpClient) {
        String token = request.getHeader(authHeader);
        if (token != null) {
            ResponseEntity<String> response = sendValidationRequest(token, httpClient);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        }
        return null;
    }

    /**
     * Sends a request to the gateway to authenticate the user.
     *
     * @param token the unique token per user
     * @return a response entity
     */
    public static ResponseEntity<String> sendValidationRequest(String token,
                                                               HttpClient httpClient) {
        HttpRequest request = builder
                .uri(URI.create("http://localhost:8000/validate"))
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .header("e91e6348157868de9dd8b25c81aebfb9", "0ad210d6fa2d8f4f2775dd31f7a0ca0c")
                .build();
        try {
            HttpResponse<String> response =
                    httpClient
                            .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.OK.value()) {
                return badRequest.body("Bad gateway request.");
            }
            return ok.body(response.body());
        } catch (Exception e) {
            return badRequest.body("Communication with server failed.");
        }
    }

    /**
     * Validates the given input to the food.
     *
     * @param food The food we want to validate
     * @return True if the validation criterias are met
     * @throws InvalidFoodException When the conditions are not met
     */
    public static boolean validateFood(Food food) throws InvalidFoodException {
        Validator handler1 = new NameValidator();
        Validator handler2 = new PortionsValidator();
        handler2.setNext(new PriceValidator());
        handler1.setNext(handler2);
        return handler1.handle(food);
    }

    /**
     * Changes the balance of users according to the amount of food consumed.
     *
     * @param pricePerPortion The price per portion of a given food
     * @param users The users mapped to the portions they consumed
     * @param token The JWT token of the user making the request
     * @param httpClient The client we use for communication with the gateway
     * @return Whether we successfully changed the balance of all given users
     */
    public static boolean alterCreditsOfUsers(double pricePerPortion, Map<String, Integer> users,
                                              String token, HttpClient httpClient) {
        for (String email : users.keySet()) {
            HttpRequest httpRequest = HttpRequest
                    .newBuilder()
                    .uri(URI.create("http://localhost:8000/user/alterCredits"))
                    .header(type, value)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .PUT(HttpRequest.BodyPublishers.ofString("[\""
                            + -users.get(email)
                            * pricePerPortion
                            + "\", \"" + email + "\"]"))
                    .build();
            ResponseEntity response = sendRequest(httpRequest, httpClient);
            if (response.getStatusCode().value() != HttpStatus.OK.value()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieve a list of all user emails.
     *
     * @param token The JWT token of the user making the request
     * @param httpClient The client we use for communication with the gateway
     * @return The list if we were successful, otherwise null
     */
    public static List<String> getAllUsers(String token, HttpClient httpClient) {
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/user/all"))
                .header(HttpHeaders.AUTHORIZATION, token)
                .GET()
                .build();
        ResponseEntity response = sendRequest(httpRequest, httpClient);
        if (response.getStatusCode().value() != HttpStatus.OK.value()) {
            return null;
        }

        try {
            return new ObjectMapper()
                    .readValue(response.getBody().toString(),
                            new TypeReference<ArrayList<String>>() {
                            });
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
