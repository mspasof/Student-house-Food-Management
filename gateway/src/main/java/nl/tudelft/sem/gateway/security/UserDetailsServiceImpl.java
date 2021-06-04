package nl.tudelft.sem.gateway.security;

import static java.util.Collections.emptyList;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private transient HttpClient httpClient = HttpClient.newBuilder().build();

    public UserDetailsServiceImpl() {

    }

    public UserDetailsServiceImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        java.net.http.HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8001/password/" + email))
                .header(SecurityConstants.SECRET_HEADER, SecurityConstants.SECRET_VALUE)
                .GET()
                .build();
        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpStatus.OK.value()) {
                throw new RuntimeException(Integer.toString(response.statusCode()));
            }

            return new org.springframework.security.core.userdetails
                    .User(email, response.body(), emptyList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}