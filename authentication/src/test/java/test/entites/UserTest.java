package test.entites;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import nl.tudelft.sem.authentication.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import test.Config;

@DataJpaTest
@ContextConfiguration(
        classes = {Config.class}
)
public class UserTest {
    private transient User testUser = new User("student@student.tudelft.nl",
            "password", "Student", "Test", 50);
    private transient User testUser2 = new User("newStudent@student.tudelft.nl",
            "password1", "Other", "Test1",  50);
    private transient User testUser3 = new User("student@student.tudelft.nl",
            "password2", "Other", "Test2",  50);

    @Test
    void getEmailTest() {
        assertEquals("student@student.tudelft.nl", testUser.getEmail());
    }

    @Test
    void setEmailTest() {
        testUser.setEmail("something@student.tudelft.nl");
        assertEquals("something@student.tudelft.nl", testUser.getEmail());
    }

    @Test
    void getPasswordTest() {
        assertEquals("password", testUser.getPassword());
    }

    @Test
    void setPasswordTest() {
        testUser.setPassword("newPassword");
        assertEquals("newPassword", testUser.getPassword());
    }

    @Test
    void getFirstName() {
        assertEquals("Student", testUser.getFirstName());
    }

    @Test
    void setFirstName() {
        testUser.setFirstName("Jack");
        assertEquals("Jack", testUser.getFirstName());
    }

    @Test
    void getLastNameTest() {
        assertEquals("Test", testUser.getLastName());
    }

    @Test
    void setLastNameTest() {
        testUser.setLastName("McJackson");
        assertEquals("McJackson", testUser.getLastName());
    }

    @Test
    void getCreditsTest() {
        assertEquals(50, testUser.getCredits());
    }

    @Test
    void setCreditsTest() {
        testUser.setCredits(100);
        assertEquals(100, testUser.getCredits());
    }

    @Test
    void equalsTest() {
        assertTrue(testUser.equals(testUser));
        assertThat(testUser).isNotEqualTo(null);
        assertFalse(testUser.equals(testUser2));
        assertTrue(testUser.equals(testUser3));
    }

    @Test
    void hashCodeTest() {
        int hash = Objects.hashCode(testUser.getEmail());
        int hash2 = Objects.hashCode(testUser2.getEmail());
        assertEquals(hash, testUser.getEmail().hashCode());
        assertThat(hash2).isNotEqualTo(testUser.getEmail().hashCode());
    }
}
