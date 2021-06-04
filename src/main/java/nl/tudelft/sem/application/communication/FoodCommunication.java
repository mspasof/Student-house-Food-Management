package nl.tudelft.sem.application.communication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Map;
import nl.tudelft.sem.application.Main;
import org.springframework.http.HttpHeaders;

public class FoodCommunication {
    private static final String separator = "\", \"";
    private static final String type = "Content-Type";
    private static final String value = "application/json";

    /**
     * Sends a request to the API Gateway to retrieve
     * the ids of all foods from the database.
     *
     * @param token the authentication token of the user
     * @return a response
     */
    public static String all(String token) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/food/all"))
                .header(type, value)
                .header(HttpHeaders.AUTHORIZATION, token)
                .GET()
                .build();
        return Main.sendRequest(request);
    }

    /**
     * Sends a request to the API Gateway to add a food to the database.
     *
     * @param name the name of the food
     * @param portions the number of portions which the food can be split into
     * @param price the total price of the food
     * @param token the authentication token of the user
     * @return a response
     */
    public static String add(String name, int portions, double price, String token) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/food/add"))
                .header(type, value)
                .header(HttpHeaders.AUTHORIZATION, token)
                .POST(HttpRequest.BodyPublishers
                        .ofString("[\"" + name + separator + portions
                                + separator + price + "\"]"))
                .build();
        return Main.sendRequest(request);
    }

    /**
     * Sends a request to the API Gateway to retrieve a food with a given id.
     *
     * @param id the id of the food
     * @param token the authentication token of the user
     * @return a response
     */
    public static String findById(long id, String token) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/food/get/" + id))
                .header(type, value)
                .header(HttpHeaders.AUTHORIZATION, token)
                .GET()
                .build();
        return Main.sendRequest(request);
    }

    /**
    * Sends a request to the API Gateway to update a given attribute of a given food.
    *
    * @param id the id of the food
    * @param newName the new name of the food
    * @param token the authentication token of the user.
    * @return a response
    */
    public static String changeName(long id, String newName, String token) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/food/changeName"))
                .header(type, value)
                .header(HttpHeaders.AUTHORIZATION, token)
                .PUT(HttpRequest.BodyPublishers
                             .ofString("[\"" + id + separator + newName + "\"]"))
                .build();
        return Main.sendRequest(request);
    }

    /**
     * Sends a request to the API Gateway to delete a food with a given id from the database.
     *
     * @param id the id of the food
     * @param token the authentication token of the user
     * @return a response
     */
    public static String delete(long id, String strategy, String token) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/food/delete/" + id + "/" + strategy))
                .header(type, value)
                .header(HttpHeaders.AUTHORIZATION, token)
                .DELETE()
                .build();
        return Main.sendRequest(request);
    }

    /**
     * Sends a request to the API Gateway to remove all foods from the database.
     *
     * @param token the authentication token of the user
     * @return a response
     */
    public static String reset(String token) {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/food/reset"))
                .header(type, value)
                .header(HttpHeaders.AUTHORIZATION, token)
                .DELETE()
                .build();
        return Main.sendRequest(request);
    }

    /**
     * Sends a request to the API Gateway to removes portions of food consumed
     * by the current users and some other users and alters their credits.
     *
     * @param id the id of the food
     * @param portions the number of portions consumed by the current user
     * @param others the other users that have eaten the food
     *                       and the amount of portions each onr of them has eaten
     * @param token the authentication token of the user
     * @return a response
     */
    public static String consume(long id, int portions,
                                     Map<String, Integer> others,
                                     String token) {
        String body = "{";
        for (String email : others.keySet()) {
            body += "\"" + email + "\":\"" + others.get(email) + "\",";
        }
        if (!others.entrySet().isEmpty()) {
            body = body.substring(0, body.length() - 1);
        }
        body += "}";
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8000/food/consume/" + id + "/" + portions))
                .header(type, value)
                .header(HttpHeaders.AUTHORIZATION, token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return Main.sendRequest(request);
    }
}
