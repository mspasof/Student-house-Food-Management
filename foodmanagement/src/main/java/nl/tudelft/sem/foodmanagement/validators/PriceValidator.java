package nl.tudelft.sem.foodmanagement.validators;

import nl.tudelft.sem.foodmanagement.entities.Food;

public class PriceValidator extends BaseValidator {
    @Override
    public boolean handle(Food food) throws InvalidFoodException {
        if (food.getPrice() < 0) {
            throw new InvalidFoodException("Price should be a non-negative number.");
        }
        return super.checkNext(food);
    }
}
