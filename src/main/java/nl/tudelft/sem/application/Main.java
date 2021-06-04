package nl.tudelft.sem.application;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.http.HttpStatus;

public class Main {
    /**
     * Sends a request to the API Gateway.
     *
     * @param request the request to be sent
     * @return a response
     */
    public static String sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response =
                    HttpClient.newBuilder().build()
                            .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpStatus.OK.value()) {
                return "Bad request.";
            }
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return "Communication with server failed.";
        }
    }

    public static void main(String[] args) {
    }
}
