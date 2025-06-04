package todo.utils;

public class TodoUtils {

    public static String generateTodoId() {
        return java.util.UUID.randomUUID().toString();
    }

}
