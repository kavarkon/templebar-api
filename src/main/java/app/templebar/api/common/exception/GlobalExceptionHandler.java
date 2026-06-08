package app.templebar.api.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
        LoggerFactory.getLogger(
                GlobalExceptionHandler.class
        );

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEventNotFound() {

        return new ErrorResponse(
                "EVENT_NOT_FOUND",
                "Event not found",
                OffsetDateTime.now()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound() {

        return new ErrorResponse(
                "USER_NOT_FOUND",
                "User not found",
                OffsetDateTime.now()
        );
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidPassword() {

        return new ErrorResponse(
                "INVALID_PASSWORD",
                "Invalid password",
                OffsetDateTime.now()
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExists() {

        return new ErrorResponse(
                "USER_ALREADY_EXISTS",
                "User already exists",
                OffsetDateTime.now()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationError(
            MethodArgumentNotValidException exception
    ) {

        String message = exception
                .getBindingResult()
                .getFieldErrors()
                .getFirst()
                .getDefaultMessage();

        return new ErrorResponse(
                "VALIDATION_ERROR",
                message,
                OffsetDateTime.now()
                );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedError(
            Exception exception
            ) {

        log.error(
                "Unexpected error",
                exception
                );

        return new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Unexpected error",
                OffsetDateTime.now()
                );
            }
}
