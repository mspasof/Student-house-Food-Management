package nl.tudelft.sem.foodmanagement.validators;

import nl.tudelft.sem.foodmanagement.entities.Food;

public class PortionsValidator extends BaseValidator {

    @Override
    public boolean handle(Food food) throws InvalidFoodException {
        if (food.getPortions() <= 0) {
            throw new InvalidFoodException("Number of portions should be a positive integer.");
        }
        return super.checkNext(food);
    }
}
