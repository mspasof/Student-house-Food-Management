package nl.tudelft.sem.foodmanagement.controllers.strategies;

import java.net.http.HttpClient;
import nl.tudelft.sem.foodmanagement.repositories.FoodRepository;

public class NoChargeStrategy implements DeleteStrategy {

    /**
     * Delete a food from the database,
     * where the person who does the deleting will not be charged for it.
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
        foodRepository.deleteById(id);
    }
}
