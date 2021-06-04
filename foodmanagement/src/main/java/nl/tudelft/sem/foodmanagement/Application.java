package nl.tudelft.sem.foodmanagement;

import java.net.http.HttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    @Bean
    public HttpClient client() {
        return HttpClient.newBuilder().build();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
