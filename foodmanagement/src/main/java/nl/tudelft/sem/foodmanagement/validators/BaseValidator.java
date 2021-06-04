package nl.tudelft.sem.foodmanagement.validators;

import nl.tudelft.sem.foodmanagement.entities.Food;

public abstract class BaseValidator implements Validator {
    private transient Validator next;

    public void setNext(Validator h) {
        this.next = h;
    }

    protected boolean checkNext(Food food) throws InvalidFoodException {
        if (next == null) {
            return true;
        }
        return next.handle(food);
    }
}
