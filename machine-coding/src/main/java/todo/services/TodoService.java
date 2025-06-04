package todo.services;

import todo.exceptions.TodoException;
import todo.exceptions.TodoValidationException;
import todo.models.TodoInfo;
import todo.models.TodoNode;

public interface TodoService {

    TodoNode createList(TodoInfo todoInfo) throws TodoValidationException;
    TodoNode createList(String todoId, String nodeId, TodoInfo todoInfo) throws TodoException, TodoValidationException;

    TodoNode createItem(TodoInfo todoInfo) throws TodoValidationException;
    TodoNode createItem(String todoId, String nodeId, TodoInfo todoInfo) throws TodoException, TodoValidationException;

    TodoNode get(String todoId) throws TodoException;

    void display(String todoId) throws TodoException;

}
