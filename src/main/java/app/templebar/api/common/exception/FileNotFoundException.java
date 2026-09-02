package app.templebar.api.common.exception;

public class FileNotFoundException
        extends RuntimeException {

    public FileNotFoundException() {
        super("File not found");
    }
}
