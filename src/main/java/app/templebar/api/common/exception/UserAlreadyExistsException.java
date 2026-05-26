package app.templebar.api.common.exception;

public class UserAlreadyExistsException
        extends RuntimeException {

    public UserAlreadyExistsException() {
        super("User already exists");
    }
}
