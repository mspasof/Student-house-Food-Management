package nl.tudelft.sem.gateway.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.gateway.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class AuthenticationRoute {
    private static final String type = "Content-Type";
    private static final String value = "application/json";
    private final transient String authHeader = "Authorization";
    public static final String separator = "\", \"";

    @Autowired
    private transient HttpClient httpClient;

    /**
     * Sends a request to the authentication microservice
     * to retrieve the emails of all users from the database.
     *
     * @return a response entity
     */
    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<String> getAllUsers(HttpServletRequest servletRequest) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8001/all"))
                .header(type, value)
                .header(authHeader, servletRequest.getHeader(authHeader))
                .GET()
                .build();
        return Application.sendRequest(request, httpClient);
    }

    /**
     * Sends a request to the authentication microservice
     * to register a new user.
     *
     * @param params the parameters of the request
     * @return a response entity
     */
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<String> register(@RequestBody List<String> params) {
        String email = params.get(0);
        String password = params.get(1);
        String firstName = params.get(2);
        String lastName = params.get(3);
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8001/register"))
                .header(type, value)
                .POST(HttpRequest.BodyPublishers
                        .ofString("[\"" + email + separator + password
                                + separator + firstName + separator + lastName + "\"]"))
                .build();
        return Application.sendRequest(request, httpClient);
    }

    /**
     * Sends a request to the authentication microservice
     * to change the password of a given user.
     *
     * @param newPassword the new password of the user
     * @param servletRequest a servlet request
     * @return a response entity
     */
    @PutMapping("/changePassword")
    @ResponseBody
    public ResponseEntity<String> changePassword(@RequestBody String newPassword,
                                                 HttpServletRequest servletRequest) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8001/changePassword"))
                .header(type, value)
                .header(authHeader, servletRequest.getHeader(authHeader))
                .PUT(HttpRequest.BodyPublishers
                        .ofString(newPassword))
                .build();
        return Application.sendRequest(request, httpClient);
    }

    /**
     * Sends a request to the authentication microservice
     * to delete a given user from the database.
     *
     * @param servletRequest a servlet request
     * @return a response entity
     */
    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteUser(HttpServletRequest servletRequest) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8001/delete"))
                .header(type, value)
                .header(authHeader, servletRequest.getHeader(authHeader))
                .DELETE()
                .build();
        return Application.sendRequest(request, httpClient);
    }

    /**
     * Sends a request to the authentication microservice
     * to get the credits of a given user from the database.
     *
     * @param servletRequest a servlet request
     * @return a response entity
     */
    @GetMapping("/getCredits")
    @ResponseBody
    public ResponseEntity<String> getCredits(HttpServletRequest servletRequest) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8001/getCredits"))
                .header(type, value)
                .header(authHeader, servletRequest.getHeader(authHeader))
                .GET()
                .build();
        return Application.sendRequest(request, httpClient);
    }

    /**
     * Sends a request to the authentication microservice
     * to change the amount of credits of a given user.
     *
     * @param params the parameters of the request
     * @param servletRequest a servlet request
     * @return a response entity
     */
    @PutMapping("/alterCredits")
    @ResponseBody
    public ResponseEntity<String> alterCredits(@RequestBody List<String> params,
                                               HttpServletRequest servletRequest) {
        String credits = params.get(0);
        params.remove(0);
        String user = params.isEmpty() ? servletRequest.getRemoteUser() : params.get(0);
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8001/alterCredits"))
                .header(type, value)
                .headers(authHeader, servletRequest.getHeader(authHeader))
                .PUT(HttpRequest.BodyPublishers
                        .ofString("[\"" + credits + separator + user + "\"]"))
                .build();
        return Application.sendRequest(request, httpClient);
    }

    /**
     * Resets the credits of all users in the database.
     *
     * @param servletRequest the request that is received
     * @return a response
     */
    @DeleteMapping("/reset")
    @ResponseBody
    public ResponseEntity<String> reset(HttpServletRequest servletRequest) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8001/reset"))
                .header(type, value)
                .headers(authHeader, servletRequest.getHeader(authHeader))
                .DELETE()
                .build();
        return Application.sendRequest(request, httpClient);
    }
}
