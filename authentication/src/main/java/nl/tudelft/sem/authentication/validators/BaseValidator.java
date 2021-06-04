package nl.tudelft.sem.authentication.validators;

import nl.tudelft.sem.authentication.entities.User;

public abstract class BaseValidator implements Validator {
    private transient Validator next;

    public void setNext(Validator h) {
        this.next = h;
    }

    protected boolean checkNext(User user) throws InvalidUserException {
        if (next == null) {
            return true;
        }
        return next.handle(user);
    }
}
