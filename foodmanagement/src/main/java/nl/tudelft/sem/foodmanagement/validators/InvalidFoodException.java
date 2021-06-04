package nl.tudelft.sem.foodmanagement.validators;

public class InvalidFoodException extends Exception {
    static final long serialVersionUID = 42L;

    public InvalidFoodException(String message) {
        super(message);
    }
}
