package test.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.authentication.entities.User;
import nl.tudelft.sem.authentication.repositories.AuthenticationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import test.Config;

@DataJpaTest
@ContextConfiguration(
        classes = {Config.class}
)
public class AuthenticationRepositoryTest {
    @Autowired
    private transient AuthenticationRepository authenticationRepository;

    private transient User user = new User("email", "password", "firstName", "lastName", 50);

    @BeforeEach
    void setUp() {
        authenticationRepository.save(user);
    }

    @Test
    void findByEmailTest() {
        assertEquals(user, authenticationRepository.findByEmail(user.getEmail()).get());
    }
}
