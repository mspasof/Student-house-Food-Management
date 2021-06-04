package nl.tudelft.sem.authentication.validators;

import nl.tudelft.sem.authentication.entities.User;

public class NameValidator extends BaseValidator {
    private static final transient int maxNameLength = 255;

    @Override
    public boolean handle(User user) throws InvalidUserException {

        checkFirstName(user);
        checkLastName(user);

        return super.checkNext(user);
    }

    private void checkFirstName(User user) throws InvalidUserException {
        if (user.getFirstName().length() > maxNameLength) {
            throw new InvalidUserException("The first name must be at most 255 characters long.");
        } else if (!Character.isUpperCase(user.getFirstName().charAt(0))
                || !user.getFirstName().substring(1).chars().allMatch(Character::isLowerCase)) {
            throw new InvalidUserException("The first name must start with a capital letter"
                    + " and only contain lowercase letters afterwards.");
        }
    }

    private void checkLastName(User user) throws InvalidUserException {
        if (user.getLastName().length() > maxNameLength) {
            throw new InvalidUserException("The last name must be at most 255 characters long.");
        } else if (!user.getLastName().chars()
                .allMatch(character -> Character.isAlphabetic(character)
                        || Character.isWhitespace(character))) {
            throw new InvalidUserException(
                    "The last name must only contain letters and empty spaces.");
        }
    }
}
