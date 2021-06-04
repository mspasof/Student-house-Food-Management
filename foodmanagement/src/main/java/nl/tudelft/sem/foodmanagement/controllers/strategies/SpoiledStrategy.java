package nl.tudelft.sem.foodmanagement.controllers.strategies;

import static nl.tudelft.sem.foodmanagement.controllers.FoodController.sendRequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.foodmanagement.entities.Food;
import nl.tudelft.sem.foodmanagement.repositories.FoodRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SpoiledStrategy implements DeleteStrategy {

    /**
     * Delete a spoiled food from the database,
     * where all people will be charged equally for it.
     *
     * @param id - the id of the food to be deleted.
     * @param foodRepository - the food repository where it will be depleted from
     * @param token - the token of the current user
     * @param httpClient - the http client which is used for sending requests.
     * @throws Exception when something goes wrong in the adding to the database.
     */
    @Override
    public void deleteFood(long id, FoodRepository foodRepository,
                              String token, HttpClient httpClient) throws Exception {
        java.net.http.HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/user/all"))
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .GET()
                .build();
        ResponseEntity<String> response = sendRequest(httpRequest, httpClient);
        if (response.getStatusCode().value() == HttpStatus.OK.value()) {
            List<String> allEmails = new ObjectMapper()
                    .readValue(response.getBody(),
                            new TypeReference<ArrayList<String>>() {});
            for (String email : allEmails) {
                httpRequest = HttpRequest
                        .newBuilder()
                        .uri(URI.create("http://localhost:8000/user/alterCredits"))
                        .header("Content-Type", "application/json")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .PUT(HttpRequest.BodyPublishers.ofString("[\""
                                + calculateCharge(foodRepository.findById(id).get(),
                                allEmails.size()) + "\", \"" + email + "\"]"))
                        .build();
                response = sendRequest(httpRequest, httpClient);
                if (response.getStatusCode().value() != HttpStatus.OK.value()) {
                    throw new Exception("Internal server error.");
                }
            }
        }
        foodRepository.deleteById(id);
    }

    /**
     * Calculates the charge per user.
     *
     * @param food - the food that was spoiled
     * @param numberOfUsers - the number of users
     * @return the charge per user
     */
    public double calculateCharge(Food food, int numberOfUsers) {
        return (-food.getPrice() * food.getPortions()) / numberOfUsers;
    }


}
