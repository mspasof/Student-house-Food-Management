package nl.tudelft.sem.authentication.validators;

import nl.tudelft.sem.authentication.entities.User;

public interface Validator {
    void setNext(Validator handler);

    boolean handle(User user) throws InvalidUserException;
}
