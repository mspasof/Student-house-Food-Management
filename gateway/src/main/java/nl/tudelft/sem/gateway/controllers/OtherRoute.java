package nl.tudelft.sem.gateway.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.gateway.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class OtherRoute {
    @Autowired
    private transient BCryptPasswordEncoder encoder;

    @Autowired
    private transient HttpClient httpClient;

    private final transient  String authHeader = "Authorization";
    private final transient String secretHeader = "e91e6348157868de9dd8b25c81aebfb9";
    private final transient  String secretValue = "0ad210d6fa2d8f4f2775dd31f7a0ca0c";
    private final transient ResponseEntity.BodyBuilder badRequest = ResponseEntity.badRequest();

    /**
     * Hashes a given string.
     * THIS METHOD IS USED ONLY BY THE API GATEWAY FOR AUTHORIZATION!!!
     *
     * @param string the string to be hashed
     */
    @PostMapping("hash")
    @ResponseBody
    public ResponseEntity<String> hash(@RequestBody String string,
                                       HttpServletRequest servletRequest) {
        if (servletRequest.getHeader(secretHeader) == null
                || !servletRequest.getHeader(secretHeader).equals(secretValue)) {
            return badRequest.body("Unauthorized access.");
        }
        return ResponseEntity.ok(encoder.encode(string));
    }

    /**
     * Used to validate a user that sends requests to the other microservices.
     * THIS METHOD IS USED ONLY BY THE API GATEWAY FOR AUTHORIZATION!!!
     *
     * @param servletRequest the request that is received
     * @return a response
     */
    @GetMapping("validate")
    @ResponseBody
    public ResponseEntity<String> validate(HttpServletRequest servletRequest) {
        if (servletRequest.getHeader(secretHeader) == null
                || !servletRequest.getHeader(secretHeader).equals(secretValue)) {
            return badRequest.body("Unauthorized access.");
        }
        return ResponseEntity.ok().body(servletRequest.getRemoteUser());
    }

    /**
     * Resets the credits of all users in the database
     * and removes all foods from the database.
     *
     * @param servletRequest the request that is received
     * @return a response
     */
    @DeleteMapping("reset")
    @ResponseBody
    public ResponseEntity<String> reset(HttpServletRequest servletRequest) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8001/reset"))
                .header("Content-Type", "application/json")
                .header(authHeader, servletRequest.getHeader(authHeader))
                .DELETE()
                .build();
        ResponseEntity<String> response =  Application.sendRequest(request, httpClient);
        if (response.getStatusCode().value() != HttpStatus.OK.value()) {
            return badRequest.body("Communication with server failed.");
        }
        request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8002/reset"))
                .header("Content-Type", "application/json")
                .header(authHeader, servletRequest.getHeader(authHeader))
                .DELETE()
                .build();
        response = Application.sendRequest(request, httpClient);
        if (response.getStatusCode().value() != HttpStatus.OK.value()) {
            return badRequest.body("Communication with server failed.");
        }
        return ResponseEntity.ok("Credits reset and foods deleted.");
    }
}
