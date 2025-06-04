package todo.exceptions;

public class TodoException extends Exception {
    public TodoException(String message) {
        super(message);
    }
    public TodoException(String message, Throwable cause) {
        super(message, cause);
    }
}
