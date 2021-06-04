package nl.tudelft.sem.foodmanagement.validators;

import nl.tudelft.sem.foodmanagement.entities.Food;

public interface Validator {
    void setNext(Validator handler);

    boolean handle(Food food) throws InvalidFoodException;
}
