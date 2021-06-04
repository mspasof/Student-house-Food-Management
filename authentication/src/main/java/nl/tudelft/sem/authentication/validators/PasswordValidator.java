package nl.tudelft.sem.authentication.validators;

import nl.tudelft.sem.authentication.entities.User;

public class PasswordValidator extends BaseValidator {
    private final transient int minPasswordLength = 8;
    private final transient int maxPasswordLength = 30;

    @Override
    public boolean handle(User user) throws InvalidUserException {

        checkPasswordLengthTooShort(user);
        checkPasswordLengthTooLong(user);
        checkSpacesAndLines(user);
        checkPasswordSpecifics(user);

        return super.checkNext(user);
    }

    private void checkPasswordLengthTooShort(User user) throws InvalidUserException {
        if (user.getPassword().length() < minPasswordLength) {
            throw new InvalidUserException("The password must be at least 8 characters long.");
        }
    }

    private void checkPasswordLengthTooLong(User user) throws InvalidUserException {
        if (user.getPassword().length() > maxPasswordLength) {
            throw new InvalidUserException("The password must be at most 30 characters long.");
        }
    }

    private void checkSpacesAndLines(User user) throws InvalidUserException {
        if (user.getPassword().contains(" ") || user.getPassword().contains("\n")) {
            throw new InvalidUserException(
                    "The password must not contain empty spaces or new lines.");
        }
    }

    private void checkPasswordSpecifics(User user) throws InvalidUserException {
        if (!user.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
            throw new InvalidUserException("The password must contain at least "
                    + "one uppercase letter, one lowercase letter and one digit.");
        }
    }
}
