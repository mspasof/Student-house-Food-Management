package nl.tudelft.sem.gateway.security;

import java.net.http.HttpClient;
import java.util.Date;
import java.util.Deque;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static HttpClient client = HttpClient.newBuilder().build();

    private transient AuthenticationManager authenticationManager;

    /**
     * Constructor for the jwt authentication filter class.
     *
     * @param authenticationManager the authentication manager
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        setUsernameParameter("email");
        setFilterProcessesUrl("/user/login");
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) {
        Deque<String> credentials = JwtAuthenticationFilterUtils.dequeCreator(req);

        String email = credentials.getFirst();
        String password = credentials.getLast();

        return JwtAuthenticationFilterUtils
                .attemptAuthenticationHelper(email, password, authenticationManager);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) {
        String token = JwtAuthenticationFilterUtils2
                .createToken(auth, new Date(System.currentTimeMillis() + 864_000_000), "TU_Delft");
        res.addHeader("Authorization", "Bearer " + token);
    }
}