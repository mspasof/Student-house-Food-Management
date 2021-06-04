package nl.tudelft.sem.authentication.validators;

import nl.tudelft.sem.authentication.entities.User;
import nl.tudelft.sem.authentication.repositories.AuthenticationRepository;
import org.apache.commons.validator.routines.EmailValidator;

public class CustomEmailValidator extends BaseValidator {
    private transient AuthenticationRepository authenticationRepository;
    private transient boolean shouldExist;
    private final transient int maxEmailLength = 255;

    public CustomEmailValidator(AuthenticationRepository authenticationRepository,
                                boolean shouldExist) {
        this.authenticationRepository = authenticationRepository;
        this.shouldExist = shouldExist;
    }

    @Override
    public boolean handle(User user) throws InvalidUserException {

        checkTakenEmail(user);
        checkUserNotExisting(user);
        checkEmailLength(user);
        checkEmailValidity(user);

        return super.checkNext(user);
    }

    private void checkTakenEmail(User user) throws InvalidUserException {
        if (authenticationRepository.findByEmail(user.getEmail()).isPresent() && !shouldExist) {
            throw new InvalidUserException(
                    "This email has already been taken.");
        }
    }

    private void checkUserNotExisting(User user) throws InvalidUserException {
        if (!authenticationRepository
                .findByEmail(user.getEmail()).isPresent() && shouldExist) {
            throw new InvalidUserException("A user with this email does not exist.");
        }
    }

    private void checkEmailLength(User user) throws InvalidUserException {
        if (user.getEmail().length() > maxEmailLength) {
            throw new InvalidUserException("The email is too long.");
        }
    }

    private void checkEmailValidity(User user) throws InvalidUserException {
        if (!EmailValidator.getInstance().isValid(user.getEmail())) {
            throw new InvalidUserException("Invalid email.");
        }
    }
}
