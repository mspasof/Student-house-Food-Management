package nl.tudelft.sem.authentication.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.authentication.entities.User;
import nl.tudelft.sem.authentication.repositories.AuthenticationRepository;
import nl.tudelft.sem.authentication.validators.CustomEmailValidator;
import nl.tudelft.sem.authentication.validators.NameValidator;
import nl.tudelft.sem.authentication.validators.PasswordValidator;
import nl.tudelft.sem.authentication.validators.Validator;
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
public class AuthenticationController {
    @Autowired
    private transient HttpClient httpClient;

    @Autowired
    private transient AuthenticationRepository authenticationRepository;

    private final transient String authHeader = "Authorization";
    private final transient String forbidden = "Unauthorised access.";
    private final transient String secretHeader = "e91e6348157868de9dd8b25c81aebfb9";
    private final transient String secretValue = "0ad210d6fa2d8f4f2775dd31f7a0ca0c";

    /**
     * Sends a request to the API Gateway.
     *
     * @param request the request to be sent
     * @return a response
     */
    private ResponseEntity<String> sendRequest(HttpRequest request, HttpClient httpClient) {
        try {
            HttpResponse<String> response =
                    httpClient
                            .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpStatus.OK.value()) {
                return ResponseEntity.badRequest().body("Bad request.");
            }
            return ResponseEntity.ok(response.body());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Communication with server failed.");
        }
    }

    /**
     * Sends a request to the gateway to authenticate the user.
     *
     * @param token the unique token per user
     * @return a response entity
     */
    private ResponseEntity<String> sendValidationRequest(String token, HttpClient httpClient) {
        HttpRequest request = HttpRequest
                .newBuilder()
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
                return ResponseEntity.badRequest().body("Bad gateway request.");
            }
            return ResponseEntity.ok(response.body());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Communication with server failed.");
        }
    }

    /**
     * Retrieves the password of a user with a given email.
     * THIS METHOD IS USED ONLY BY THE API GATEWAY FOR AUTHORIZATION!!!
     *
     * @param email the email of the user
     * @return a response entity
     */
    @GetMapping("password/{email}")
    @ResponseBody
    public ResponseEntity<Object> authenticateUser(@PathVariable String email,
                                                   HttpServletRequest servletRequest) {
        if (servletRequest.getHeader(secretHeader) == null
                || !servletRequest.getHeader(secretHeader).equals(secretValue)) {
            return ResponseEntity.badRequest().body("Unauthorized access.");
        }
        Optional<User> optional = authenticationRepository.findByEmail(email);
        if (optional.isPresent()) {
            return ResponseEntity.ok(optional.get().getPassword());
        }
        return ResponseEntity.badRequest().body("A user with this username does not exist.");
    }

    /**
     * Returns the emails of all users.
     *
     * @param request the request that is received
     * @return a response
     */
    @GetMapping("all")
    @ResponseBody
    public ResponseEntity<Object> getAllUsers(HttpServletRequest request) {
        String token = request.getHeader(authHeader);
        if (token != null) {
            ResponseEntity<String> response = sendValidationRequest(token, httpClient);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                return ResponseEntity.ok(authenticationRepository
                        .findAll().stream().map(user -> user.getEmail()));
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(forbidden);
    }


    /**
     * Registers a new user in the database.
     *
     * @param params the list of parameters of the request
     * @return a response entity with information about the outcome of the transaction
     */
    @PostMapping("register")
    @ResponseBody
    public ResponseEntity<Object> register(@RequestBody List<String> params) {
        Validator handler1 = new CustomEmailValidator(this.authenticationRepository, false);
        Validator handler2 = new PasswordValidator();
        handler2.setNext(new NameValidator());
        handler1.setNext(handler2);
        try {
            if (handler1.handle(new User(params.get(0),
                    params.get(1), params.get(2), params.get(3), 0))) {
                java.net.http.HttpRequest request = HttpRequest
                        .newBuilder()
                        .uri(URI.create("http://localhost:8000/hash"))
                        .header("Content-Type", "application/json")
                        .header(secretHeader, secretValue)
                        .POST(HttpRequest.BodyPublishers
                                .ofString(params.get(1)))
                        .build();
                ResponseEntity<String> response = sendRequest(request, httpClient);
                if (response.getStatusCode().value() != HttpStatus.OK.value()) {
                    return ResponseEntity
                            .badRequest()
                            .body("{ \"error\": \"Communication with gateway failed.\" }");
                }
                User user = new User(params.get(0), response.getBody(),
                        params.get(2), params.get(3), 0);
                authenticationRepository.save(user);
                return ResponseEntity.ok(user);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.badRequest().body("");
    }

    /**
     * Changes the password of a user with a given email.
     *
     * @param newPassword the new password of the user
     * @return a response entity
     */
    @PutMapping("changePassword")
    @ResponseBody
    public ResponseEntity<Object> changePassword(@RequestBody String newPassword,
                                                 HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader(authHeader);
        if (token != null) {
            ResponseEntity<String> response = sendValidationRequest(token, httpClient);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                Validator emailValidator = new CustomEmailValidator(
                        this.authenticationRepository, true);
                emailValidator.setNext(new PasswordValidator());
                try {
                    if (emailValidator.handle(new User(response.getBody(),
                            newPassword, "", "", 0))) {
                        java.net.http.HttpRequest request = HttpRequest
                                .newBuilder()
                                .uri(URI.create("http://localhost:8000/hash"))
                                .header("Content-Type", "application/json")
                                .header(secretHeader, secretValue)
                                .POST(HttpRequest.BodyPublishers
                                        .ofString(newPassword))
                                .build();
                        ResponseEntity<String> response1 = sendRequest(request, httpClient);
                        if (response1.getStatusCode().value() != HttpStatus.OK.value()) {
                            return ResponseEntity
                                    .badRequest()
                                    .body("{ \"error\": \"Communication with gateway failed.\" }");
                        }
                        User user = authenticationRepository.findByEmail(response.getBody()).get();
                        user.setPassword(response1.getBody());
                        authenticationRepository.save(user);
                        return ResponseEntity
                                .ok("{\"Success\":\"Password has been successfully changed.\"}");
                    }
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(forbidden);
    }

    /**
     * Deletes a user from the database.
     *
     * @return a response entity
     */
    @DeleteMapping("delete")
    @ResponseBody
    public ResponseEntity<Object> deleteUser(HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader(authHeader);
        if (token != null) {
            ResponseEntity<String> servletResponse = sendValidationRequest(token, httpClient);
            if (servletResponse.getStatusCode().value() == HttpStatus.OK.value()) {
                authenticationRepository.deleteById(servletResponse.getBody());
                return ResponseEntity
                        .ok("{\" Success:\":\"User deleted successfully.\"}");
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(forbidden);
    }

    /**
     * Gets the credits of a user from the database.
     *
     * @return a response entity
     */
    @GetMapping("getCredits")
    public ResponseEntity<Object> getCredits(HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader(authHeader);
        if (token != null) {
            ResponseEntity<String> servletResponse = sendValidationRequest(token, httpClient);
            if (servletResponse.getStatusCode().value() == HttpStatus.OK.value()) {
                Optional<User> user = authenticationRepository
                        .findByEmail(servletResponse.getBody());
                if (!user.isPresent()) {
                    return ResponseEntity.badRequest()
                            .body("A user with this id is not present in the database.");
                }
                return ResponseEntity.ok("Credits: " + user.get().getCredits());
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(forbidden);
    }

    /**
     * Changes the amount of credits of a user by a given amount.
     *
     * @param params the parameters of the request
     * @return a response entity
     */
    @PutMapping("alterCredits")
    @ResponseBody
    public ResponseEntity<Object> alterCredits(@RequestBody List<String> params,
                                               HttpServletRequest request) {
        String token = request.getHeader(authHeader);
        if (token != null) {
            ResponseEntity<String> response = sendValidationRequest(token, httpClient);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                try {
                    double credits = Double.parseDouble(params.get(0));
                    params.remove(0);
                    String email = params.isEmpty() ? response.getBody() : params.get(0);
                    User user = authenticationRepository.findByEmail(email).get();
                    user.setCredits(credits + user.getCredits());
                    authenticationRepository.save(user);
                    return ResponseEntity.ok("Credits updated.");
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body("Invalid credits.");
                } catch (Exception e) {
                    return ResponseEntity.badRequest()
                            .body("A user with this email does not exist.");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(forbidden);
    }

    /**
     * Resets the credits of all users in the database.
     *
     * @param request the request that is received
     * @return a response
     */
    @DeleteMapping("reset")
    @ResponseBody
    public ResponseEntity<Object> reset(HttpServletRequest request) {
        String token = request.getHeader(authHeader);
        if (token != null) {
            ResponseEntity<String> response = sendValidationRequest(token, httpClient);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                for (String email : authenticationRepository.findAll()
                        .stream().map(user -> user.getEmail()).collect(Collectors.toList())) {
                    User user = authenticationRepository.findByEmail(email).get();
                    user.setCredits(0);
                    authenticationRepository.save(user);
                }
                return ResponseEntity.ok("Credits reset.");
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(forbidden);
    }
}
