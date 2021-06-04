package nl.tudelft.sem.application.communication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import nl.tudelft.sem.application.Main;

public class OtherCommunication {
    private static HttpClient client = HttpClient.newBuilder().build();

    /**
     * Removes all foods from the database
     * and resets the credits of all users.
     *
     * @param token the authentication token of the user
     * @return a response
     */
    public static String reset(String token) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/reset"))
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .DELETE()
                .build();
        return Main.sendRequest(request);
    }
}
