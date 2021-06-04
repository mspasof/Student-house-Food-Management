package nl.tudelft.sem.foodmanagement.validators;

import nl.tudelft.sem.foodmanagement.entities.Food;

public class NameValidator extends BaseValidator {
    private static final transient int maxNameLength = 255;

    @Override
    public boolean handle(Food food) throws InvalidFoodException {
        if (food.getName().length() == 0) {
            throw new InvalidFoodException("Name field must not be empty.");
        } else if (food.getName().length() >= maxNameLength) {
            throw new InvalidFoodException("Name field must be less than 255 characters long.");
        }
        return super.checkNext(food);
    }
}
