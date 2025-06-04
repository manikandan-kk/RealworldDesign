package todo.repository;

import todo.models.TodoInfo;
import todo.models.TodoNode;

import java.util.Optional;

public interface TodoRepo {

    TodoNode createList(TodoInfo todoInfo);
    TodoNode createList(String todoId, String nodeId, TodoInfo todoInfo);

    TodoNode createItem(TodoInfo todoInfo);
    TodoNode createItem(String todoId, String nodeId, TodoInfo todoInfo);

    Optional<TodoNode> get(String todoId);
    Optional<TodoNode> get(String todoId, String nodeId);

}
