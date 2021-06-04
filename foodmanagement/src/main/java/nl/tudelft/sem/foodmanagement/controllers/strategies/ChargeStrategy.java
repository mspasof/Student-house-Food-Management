package nl.tudelft.sem.foodmanagement.controllers.strategies;

import static nl.tudelft.sem.foodmanagement.controllers.FoodController.sendRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import nl.tudelft.sem.foodmanagement.entities.Food;
import nl.tudelft.sem.foodmanagement.repositories.FoodRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ChargeStrategy implements DeleteStrategy {

    /**
     * Delete a food from the database,
     * where the person who does the deleting will be charged for it.
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
        Food food = foodRepository.findById(id).get();
        double charge = calculateCharge(food);
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/user/alterCredits"))
                .header("Content-Type", "application/json")
                .header(HttpHeaders.AUTHORIZATION, token)
                .PUT(HttpRequest.BodyPublishers.ofString("[\""
                        +  charge + "\"]"))
                .build();
        ResponseEntity<String> response = sendRequest(httpRequest, httpClient);
        if (response.getStatusCode().value() != HttpStatus.OK.value()) {
            throw new Exception("Internal server error.");
        }
        foodRepository.deleteById(id);
    }

    /**
     * This method calculates the correct charge for the user.
     * This method is made public so it can be tested
     *
     * @param food - the food for which the charge is calculated.
     * @return The total cost of the food that is still left
     */
    public double calculateCharge(Food food) {
        return - food.getPrice() * food.getPortions();
    }
}
