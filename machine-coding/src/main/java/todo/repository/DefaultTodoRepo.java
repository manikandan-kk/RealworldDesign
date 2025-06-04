package todo.repository;

import todo.logic.TodoNodeIdentifier;
import todo.models.TodoInfo;
import todo.models.TodoItem;
import todo.models.TodoList;
import todo.models.TodoNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultTodoRepo implements TodoRepo {

    private Map<String, TodoNode> todoRepository;
    private TodoNodeIdentifier todoNodeIdentifier;

    public DefaultTodoRepo(TodoNodeIdentifier todoNodeIdentifier) {
        this.todoRepository = new HashMap<>();
        this.todoNodeIdentifier = todoNodeIdentifier;
    }

    @Override
    public TodoNode createList(TodoInfo todoInfo) {
        TodoList todoList = new TodoList(todoInfo);
        todoRepository.put(todoList.getId(), todoList);
        return todoList;
    }

    @Override
    public TodoNode createList(String todoId, String nodeId, TodoInfo todoInfo) {
        TodoList todoList = new TodoList(todoInfo);
        TodoNode parentNode = get(todoId, nodeId).get();
        ((TodoList) parentNode).getSubTodoNodes().add(todoList);
        return todoList;
    }

    @Override
    public TodoNode createItem(TodoInfo todoInfo) {
        TodoItem todoItem = new TodoItem(todoInfo);
        todoRepository.put(todoItem.getId(), todoItem);
        return todoItem;
    }

    @Override
    public TodoNode createItem(String todoId, String nodeId, TodoInfo todoInfo) {
        TodoItem todoItem = new TodoItem(todoInfo);
        TodoNode parentNode = get(todoId, nodeId).get();
        ((TodoList) parentNode).getSubTodoNodes().add(todoItem);
        return todoItem;
    }

    @Override
    public Optional<TodoNode> get(String todoId) {
        return Optional.of(todoRepository.get(todoId));
    }

    @Override
    public Optional<TodoNode> get(String todoId, String nodeId) {
        TodoNode rootNode = todoRepository.get(todoId);
        if (rootNode == null) {
            return Optional.empty();
        }
        return todoNodeIdentifier.identify(rootNode, nodeId);
    }
}
