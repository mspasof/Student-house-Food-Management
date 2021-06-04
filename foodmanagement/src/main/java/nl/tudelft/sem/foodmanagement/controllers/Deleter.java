package nl.tudelft.sem.foodmanagement.controllers;

import java.net.http.HttpClient;
import nl.tudelft.sem.foodmanagement.controllers.strategies.DeleteStrategy;
import nl.tudelft.sem.foodmanagement.repositories.FoodRepository;
import nl.tudelft.sem.foodmanagement.validators.InvalidFoodException;

public class Deleter {
    private transient DeleteStrategy deleteStrategy;

    public Deleter(DeleteStrategy deleteStrategy) {
        this.deleteStrategy = deleteStrategy;
    }

    /**
     * Deletes a food on a given strategy - spoiled or not spoiled.
     *
     * @param id the food id
     * @param foodRepository the food repository
     * @param token the user's bearer token
     * @param httpClient the http client
     * @throws Exception when the task has not been completed successfully
     */
    public void delete(long id, FoodRepository foodRepository,
                          String token, HttpClient httpClient) throws Exception {
        if (!foodRepository.existsById(id)) {
            throw new InvalidFoodException("Food with the given"
                    + " id does not exist in the database.");
        }
        this.deleteStrategy.deleteFood(id, foodRepository, token, httpClient);
    }
}
