package app.templebar.api.common.exception;

public class UserNotFoundException
        extends RuntimeException {

    public UserNotFoundException() {
        super("User not found");
    }
}
