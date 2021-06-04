package nl.tudelft.sem.foodmanagement.controllers;

import static nl.tudelft.sem.foodmanagement.controllers.Utils.alterCreditsOfUsers;
import static nl.tudelft.sem.foodmanagement.controllers.Utils.authenticate;
import static nl.tudelft.sem.foodmanagement.controllers.Utils.checkPortions;
import static nl.tudelft.sem.foodmanagement.controllers.Utils.getAllUsers;
import static nl.tudelft.sem.foodmanagement.controllers.Utils.validateFood;
import static org.springframework.http.ResponseEntity.BodyBuilder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.foodmanagement.entities.Food;
import nl.tudelft.sem.foodmanagement.repositories.FoodRepository;
import nl.tudelft.sem.foodmanagement.validators.InvalidFoodException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class FoodController {

    @Autowired
    private transient HttpClient httpClient;

    @Autowired
    private transient FoodRepository foodRepository;

    private final transient String authHeader = "Authorization";
    private static final transient String forbidden = "Unauthorised access.";
    private final transient String error = "Error.";
    private static final transient BodyBuilder badRequest = ResponseEntity.badRequest();
    private static final transient BodyBuilder ok = ResponseEntity.ok();
    private static final transient BodyBuilder internalServerError = ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR);
    private static final transient ResponseEntity<Object> unauthorised = ResponseEntity
            .status(HttpStatus.FORBIDDEN.value()).body(forbidden);
    private static final transient ResponseEntity notFound = ResponseEntity
            .status(HttpStatus.NOT_FOUND).body("Food does not exist.");

    /**
     * Sends a request to the API Gateway.
     *
     * @param request the request to be sent
     * @return a response
     */
    public static ResponseEntity<String> sendRequest(HttpRequest request, HttpClient httpClient) {
        try {
            HttpResponse<String> response =
                    httpClient
                            .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpStatus.OK.value()) {
                return badRequest.body("Bad request.");
            }
            return ok.body(response.body());
        } catch (Exception e) {
            return badRequest.body("Communication with server failed.");
        }
    }

    /**
     * Retrieves all items in the food database if the user is authenticated.
     *
     * @param request The request from which we infer the user
     * @return Json of all foods
     */
    @GetMapping("all")
    @ResponseBody
    public ResponseEntity<Object> getAllFood(HttpServletRequest request) {
        if (authenticate(request, httpClient) == null) {
            return unauthorised;
        }
        return ok.body(foodRepository.findAll()
                .stream().map(food -> food.getId()));
    }

    /**
     * Adds a food to the database.
     *
     * @param params the parameters of the request
     * @return a response entity
     */
    @PostMapping("add")
    @ResponseBody
    public ResponseEntity<Object> addFood(@RequestBody List<String> params,
                                          HttpServletRequest request) {
        String user = authenticate(request, httpClient);
        if (user == null) {
            return unauthorised;
        }
        String name = params.get(0);
        try {
            int portions = Integer.parseInt(params.get(1));
            double price = Double.parseDouble(params.get(2));
            Food food = new Food(name, portions, price / portions);
            if (validateFood(food)) {
                Map<String, Integer> users = new HashMap<>();
                users.put(user, food.getPortions());
                if (!alterCreditsOfUsers(-food.getPrice(), users,
                        request.getHeader(authHeader), httpClient)) {
                    return internalServerError.body(error);
                }
                foodRepository.save(food);
                return ok.body(food);
            }
        } catch (InvalidFoodException e) {
            return badRequest.body(e.getMessage());
        } catch (NumberFormatException e) {
            return badRequest
                    .body("Incompatible parameters passed to the request.");
        }
        return badRequest.body("Invalid food.");
    }

    /**
     * Retrieves a food with a given id from the database.
     *
     * @param id the id of the food
     * @return a response entity
     */
    @GetMapping("get/{id}")
    @ResponseBody
    public ResponseEntity<Object> findById(@PathVariable long id, HttpServletRequest request) {
        if (authenticate(request, httpClient) == null) {
            return unauthorised;
        }
        Optional<Food> food = foodRepository.findById(id);
        if (food.isPresent()) {
            return ok.body(food.get());
        }
        return badRequest.body("No food with such id.");
    }

    /**
     * Updates a given attribute of a given food in the database.
     *
     * @param params the parameters of the request
     * @return a response entity
     */
    @PutMapping("changeName")
    @ResponseBody
    public ResponseEntity<Object> changeName(@RequestBody List<String> params,
                                             HttpServletRequest request) {
        if (authenticate(request, httpClient) == null) {
            return unauthorised;
        }
        try {
            long id = Long.parseLong(params.get(0));
            Optional<Food> tempFood = foodRepository.findById(id);
            if (!tempFood.isPresent()) {
                return notFound;
            }
            Food food = tempFood.get();
            food.setName(params.get(1));
            if (validateFood(food)) {
                foodRepository.save(food);
                return ok.body("Name updated.");
            }
        } catch (InvalidFoodException e) {
            return badRequest.body(e.getMessage());
        } catch (NumberFormatException exception) {
            return badRequest
                    .body("Incompatible parameters passed to the request.");
        }
        return badRequest.body("Invalid food.");
    }

    /**
     * Deletes a certain item in the DB if the id is valid and the user is authenticated.
     *
     * @param id      The identifier of the food item
     * @param request The request from which we infer the user
     * @return A response entity
     */
    @DeleteMapping("delete/{id}/{deletionMethod}")
    @ResponseBody
    public ResponseEntity<Object> deleteFood(@PathVariable long id,
                                             @PathVariable String deletionMethod,
                                             HttpServletRequest request) {
        if (authenticate(request, httpClient) == null) {
            return unauthorised;
        }
        try {
            Deleter deleter = Utils.instantiateDeleter(deletionMethod);
            if (deleter == null) {
                return badRequest.body("This is not a valid deletion strategy.");
            }
            deleter.delete(id, foodRepository, request.getHeader(authHeader), httpClient);
            return ok.body("Food deleted successfully.");
        } catch (Exception e) {
            return badRequest.body(e.getMessage());
        }
    }

    /**
     * Deletes all foods from the database.
     */
    @DeleteMapping("reset")
    @ResponseBody
    public ResponseEntity<Object> reset(HttpServletRequest request) {
        if (authenticate(request, httpClient) == null) {
            return unauthorised;
        }
        foodRepository.deleteAll();
        foodRepository.flush();
        return ok.body("Foods deleted successfully.");
    }

    /**
     * Deletes a number of portions from a specific food from the database.
     *
     * @param foodId   the id of the food
     * @param portions the number of portions of the current user
     * @param others   the other users that have eaten the food
     *                 and the amount of portions each onr of them has eaten
     * @param request  the request that is received
     * @return the status of the request
     */
    @PostMapping("consume/{foodId}/{portions}")
    @ResponseBody
    public ResponseEntity<Object> consumePortionsOfFoodByPeople(@PathVariable("foodId") long foodId,
                                                        @PathVariable("portions") int portions,
                                                        @RequestBody Map<String, Integer> others,
                                                        HttpServletRequest request) {
        String user = authenticate(request, httpClient);
        if (user == null) {
            return unauthorised;
        }
        if (others.keySet().contains(user)) {
            return internalServerError.body(error);
        }
        others.put(user, portions);
        Optional<Food> tempFood = foodRepository.findById(foodId);
        if (tempFood.isPresent()) {
            int totalPortions = others
                    .entrySet()
                    .stream()
                    .mapToInt(entry -> entry.getValue())
                    .reduce((a, b) -> a + b)
                    .getAsInt();
            if (!checkPortions(tempFood.get(), others, totalPortions)) {
                return internalServerError.body(error);
            }
            List<String> allEmails = getAllUsers(request.getHeader(authHeader), httpClient);
            if (allEmails == null || !allEmails.containsAll(others.keySet())
                    || !alterCreditsOfUsers(tempFood.get().getPrice(),
                    others, request.getHeader(authHeader), httpClient)) {
                return internalServerError.body(error);
            }
            if (totalPortions == tempFood.get().getPortions()) {
                foodRepository.delete(tempFood.get());
                return ok.body("Food consumed.");
            }
            tempFood.get().setPortions(
                    tempFood.get().getPortions() - totalPortions);
            foodRepository.save(tempFood.get());
            return ok.body("Food portions consumed.");
        }
        return notFound;
    }
}
