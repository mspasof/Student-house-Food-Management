package nl.tudelft.sem.authentication.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "User")
public class User {
    @Id
    @Column(name = "Email")
    private String email;

    @JsonIgnore
    @Column(name = "Password")
    private String password;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "credits")
    private double credits;

    public User() {
    }

    /**
     * Create a new User instance.
     *
     * @param email User's email (unique identifier)
     * @param password Encrypted user's password
     * @param firstName User's first name
     * @param lastName User's last name
     * @param credits User's credits
     */
    public User(String email, String password, String firstName, String lastName, double credits) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.credits = credits;
    }

    /**
     * Get user email.
     *
     * @return User's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set user email.
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get user password.
     *
     * @return user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set user password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get user first name.
     *
     * @return user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set user first name.
     *
     * @param firstName user's new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get user last name.
     *
     * @return user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set user last name.
     *
     * @param lastName user's new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get user credits.
     *
     * @return user's credits
     */
    public double getCredits() {
        return credits;
    }

    /**
     * Set user credits.
     *
     * @param credits new amount of user's credits
     */
    public void setCredits(double credits) {
        this.credits = credits;
    }

    /**
     * Check whether the User instance and another are equal.
     *
     * @param o Object to compare with
     * @return Whether the provided object and this user are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        return email.equals(user.email);
    }

    /**
     * Hash user email.
     *
     * @return user's hashed email
     */
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
