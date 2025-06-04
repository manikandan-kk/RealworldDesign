package todo.exceptions;

public class TodoValidationException extends Exception {
    public TodoValidationException(String message) {
        super(message);
    }
    public TodoValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
