package app.templebar.api.common.exception;

public class EventNotFoundException
        extends RuntimeException {

    public EventNotFoundException() {
        super("Event not found");
    }
}
