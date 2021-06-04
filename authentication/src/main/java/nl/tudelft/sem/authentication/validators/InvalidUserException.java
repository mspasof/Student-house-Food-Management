package nl.tudelft.sem.authentication.validators;

public class InvalidUserException extends Exception {
    static final long serialVersionUID = 42L;

    public InvalidUserException(String message) {
        super(message);
    }
}
