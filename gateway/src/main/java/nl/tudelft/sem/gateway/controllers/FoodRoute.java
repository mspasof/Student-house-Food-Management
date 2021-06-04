package nl.tudelft.sem.gateway.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.gateway.Application;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/food")
public class FoodRoute {
    private static final transient String type = "Content-Type";
    private static final transient String value = "application/json";
    private static final transient String authHeader = "Authorization";

    @Autowired
    private transient HttpClient httpClient;

    /**
     * Sends a request to the food microservice to retrieve
     * the ids of all foods from the database.
     *
     * @return a response body
     */
    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<String> getAllFood(HttpServletRequest servletRequest) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8002/all"))
                .header(type, value)
                .header(authHeader, servletRequest.getHeader(authHeader))
                .GET()
                .build();
        return Application.sendRequest(request, httpClient);
    }

    /**
     * Sends a request to the food microservice to add a new food to the database.
     *
     * @param params the parameters of the request
     * @return a response entity
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> add(@RequestBody String params,
                                      HttpServletRequest servletRequest) {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8002/add"))
                .header(type, value)
                .header(authHeader, servletRequest.getHeader(authHeader))
                .POST(HttpRequest.BodyPublishers
                        .ofString(params))
                .build();
        return Application.sendRequest(request, httpClient);
    }

    /**
     * Sends a request to the food microservice
     * to retrieve a food with a given id from the database.
     *
     * @param id the id of the food
     * @return a response entity
     */
    @GetMapping("/get/{id}")
    @ResponseBody
    public ResponseEntity<String> findById(@PathVariable long id,
                                           HttpServletRequest servletRequest) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8002/get/" + id))
                .header(type, value)
                .header(authHeader, servletRequest.getHeader(authHeader))
                .GET()
                .build();
        return Application.sendRequest(request, httpClient);
    }

    /**
     * Sends a request to the food microservice
     * to update a given attribute of a given food in the database.
     *
     * @param params the parameters of the request
     * @return a response body
     */
    @PutMapping("/changeName")
    @ResponseBody
    public ResponseEntity<String> changeName(@RequestBody String params,
                                             HttpServletRequest servletRequest) {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8002/changeName"))
                .header(type, value)
                .header(authHeader, servletRequest.getHeader(authHeader))
                .PUT(HttpRequest.BodyPublishers
                        .ofString(params))
                .build();

        return Application.sendRequest(request, httpClient);
    }

    /**
     * Sends a request to the food microservice to delete a food with a given id from the database.
     *
     * @param id - the id of the food
     * @param deletionMethod - the way the food should be deleted
     * @return a response entity
     */
    @DeleteMapping("/delete/{id}/{deletionMethod}")
    @ResponseBody
    public ResponseEntity<String> deleteFood(@PathVariable long id,
                                             @PathVariable String deletionMethod,
                                             HttpServletRequest servletRequest) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8002/delete/" + id + "/" + deletionMethod))
                .header(type, value)
                .header(authHeader, servletRequest.getHeader(authHeader))
                .DELETE()
                .build();
        return Application.sendRequest(request, httpClient);
    }

    /**
     * Deletes all foods from the database.
     *
     * @param servletRequest the request that is received
     * @return a response
     */
    @DeleteMapping("/reset")
    @ResponseBody
    public ResponseEntity<String> reset(HttpServletRequest servletRequest) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8002/reset"))
                .header(type, value)
                .header(authHeader, servletRequest.getHeader(authHeader))
                .DELETE()
                .build();
        return Application.sendRequest(request, httpClient);
    }

    /**
     * Removes portions of food consumed by the current users
     * and some other users and alters their credits.
     *
     * @param foodId the id of the food
     * @param portions the number of portions of the current user
     * @param others the other users that have eaten the food
     *                       and the amount of portions each onr of them has eaten
     * @param servletRequest the request that is received
     * @return a response
     */
    @PostMapping("/consume/{foodId}/{portions}")
    @ResponseBody
    public ResponseEntity<String> consume(@PathVariable long foodId,
                                          @PathVariable int portions,
                                          @RequestBody String others,
                                          HttpServletRequest servletRequest) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8002/consume/" + foodId + "/" + portions))
                .header(type, value)
                .header(authHeader, servletRequest.getHeader(authHeader))
                .POST(HttpRequest.BodyPublishers.ofString(others))
                .build();
        return Application.sendRequest(request, httpClient);
    }
}
