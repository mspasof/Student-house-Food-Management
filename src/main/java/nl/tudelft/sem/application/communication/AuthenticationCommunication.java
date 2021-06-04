package nl.tudelft.sem.application.communication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import nl.tudelft.sem.application.Main;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class AuthenticationCommunication {
    private static final String separator = "\", \"";
    private static final String type = "Content-Type";
    private static final String value = "application/json";
    private static HttpClient client = HttpClient.newBuilder().build();

    /**
     * Sends a request to the API Gateway
     * to retrieve the emails of all users from the database.
     *
     * @param token the authentication token of the user
     * @return a response
     */
    public static String all(String token) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/user/all"))
                .header(type, value)
                .header(HttpHeaders.AUTHORIZATION, token)
                .GET()
                .build();
        return Main.sendRequest(request);
    }

    /**
     * Sends a request to the API Gateway to register a user.
     *
     * @param email the email of the user
     * @param password the password of the user
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @return a response
     */
    public static String register(String email, String password,
                                  String firstName, String lastName) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/user/register"))
                .header(type, value)
                .POST(HttpRequest.BodyPublishers
                        .ofString("[\"" + email + separator  + password
                                + separator + firstName + separator + lastName + "\"]"))
                .build();
        return Main.sendRequest(request);
    }

    /**
     * Sends a request to the API Gateway to change the password of the user.
     *
     * @param newPassword the new password of the user
     * @param token the authentication token of the user
     * @return a response
     */
    public static String changePassword(String newPassword, String token) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/user/changePassword"))
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(type, value)
                .PUT(HttpRequest.BodyPublishers.ofString(newPassword))
                .build();
        return Main.sendRequest(request);
    }

    /**
     * Sends a request to the API Gateway to delete a user from the database.
     *
     * @param token the authentication token of the user
     * @return a response
     */
    public static String delete(String token) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/user/delete"))
                .header(type, value)
                .header(HttpHeaders.AUTHORIZATION, token)
                .DELETE()
                .build();
        return Main.sendRequest(request);
    }

    /**
     * Sends a request to the API Gateway to get the credits of a user from the database.
     *
     * @param token the authentication token of the user
     * @return a response
     */
    public static String getCredits(String token) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/user/getCredits"))
                .header(type, value)
                .header(HttpHeaders.AUTHORIZATION, token)
                .GET()
                .build();
        return Main.sendRequest(request);
    }

    /**
     * Sends a request to the API Gateway to update the credits of the user.
     *
     * @param credits the amount of credits to be added to the user's current amount
     * @param email the email of the user whose credits are altered
     *              (null to alter the credits of user currently logged in)
     * @param token the authentication token of the user
     * @return a response
     */
    public static String alterCredits(double credits, String email, String token) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/user/credits"))
                .header(type, value)
                .header(HttpHeaders.AUTHORIZATION, token)
                .PUT(HttpRequest.BodyPublishers.ofString("[\""  + credits
                        + (email != null ? separator + email : "") + "\"]"))
                .build();
        return Main.sendRequest(request);
    }

    /**
     * Sends a request to the API Gateway to reset the credits of all users in the database.
     *
     * @param token the authentication token of the user
     * @return a response
     */
    public static String reset(String token) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/user/reset"))
                .header(type, value)
                .headers(HttpHeaders.AUTHORIZATION, token)
                .DELETE()
                .build();
        return Main.sendRequest(request);
    }

    /**
     * Sends a request to the API Gateway to login a user.
     *
     * @param email the email of the user
     * @param password the password of the user
     * @return a response
     */
    public static String login(String email, String password) {
        try {
            java.net.http.HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(URI.create("http://localhost:8000/user/login"))
                    .header(type, value)
                    .POST(HttpRequest.BodyPublishers
                            .ofString("[\"" + email + separator + password + "\"]"))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.OK.value()) {
                return "Bad request.";
            }
            return response.headers().map().get(HttpHeaders.AUTHORIZATION).get(0);
        } catch (Exception e) {
            return "Communication with server failed.";
        }
    }
}
