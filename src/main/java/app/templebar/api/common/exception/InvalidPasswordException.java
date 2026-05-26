package app.templebar.api.common.exception;

public class InvalidPasswordException
        extends RuntimeException {

    public InvalidPasswordException() {
        super("Invalid password");
    }
}
