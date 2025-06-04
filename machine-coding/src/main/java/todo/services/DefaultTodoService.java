package todo.services;

import lombok.extern.slf4j.Slf4j;
import todo.display.TodoDisplay;
import todo.exceptions.TodoException;
import todo.exceptions.TodoValidationException;
import todo.models.TodoInfo;
import todo.models.TodoNode;
import todo.repository.TodoRepo;
import todo.validator.TodoValidator;

import java.util.Optional;

import static java.lang.String.format;
import static todo.models.TodoType.LIST;

@Slf4j
public class DefaultTodoService implements TodoService {

    private final TodoRepo todoRepo;
    private final TodoValidator todoValidator;
    private final TodoDisplay todoDisplay;

    public DefaultTodoService(TodoRepo todoRepo, TodoValidator todoValidator, TodoDisplay todoDisplay) {
        this.todoRepo = todoRepo;
        this.todoValidator = todoValidator;
        this.todoDisplay = todoDisplay;
    }

    @Override
    public TodoNode createList(TodoInfo todoInfo) throws TodoValidationException {
        log.info("Creating new todo list with info: {}", todoInfo);
        validateTodoInfo(todoInfo);
        TodoNode node = todoRepo.createList(todoInfo);
        log.info("Successfully created new todo list with id: {}", node.getId());
        return node;
    }

    @Override
    public TodoNode createList(String todoId, String nodeId, TodoInfo todoInfo)
            throws TodoException, TodoValidationException {
        log.info("Creating new todo list under parent todoId: {}, nodeId: {} with info: {}", todoId, nodeId, todoInfo);
        validateTodoInfo(todoInfo);

        Optional<TodoNode> todoNodeOptional = todoRepo.get(todoId, nodeId);

        if (todoNodeOptional.isEmpty()) {
            log.error("Failed to create todo list: Parent node not found with todoId: {}, nodeId: {}", todoId, nodeId);
            throw new TodoException(format("TodoNode does not exist: %s, %s", todoId, nodeId));
        }

        TodoNode parentNode = todoNodeOptional.get();
        if (!LIST.equals(parentNode.getType())) {
            log.error("Failed to create todo list: Parent node is not a list type. todoId: {}, nodeId: {}, actual type: {}",
                todoId, nodeId, parentNode.getType());
            throw new TodoException(format("Parent node is not a list: %s, %s", todoId, nodeId));
        }

        TodoNode createdNode = todoRepo.createList(todoId, nodeId, todoInfo);
        log.info("Successfully created new todo list with id: {} under parent: {}", createdNode.getId(), todoId);
        return createdNode;
    }

    @Override
    public TodoNode createItem(TodoInfo todoInfo) throws TodoValidationException {
        log.info("Creating new todo item with info: {}", todoInfo);
        validateTodoInfo(todoInfo);
        TodoNode node = todoRepo.createItem(todoInfo);
        log.info("Successfully created new todo item with id: {}", node.getId());
        return node;
    }

    @Override
    public TodoNode createItem(String todoId, String nodeId, TodoInfo todoInfo)
            throws TodoException, TodoValidationException {
        log.info("Creating new todo item under parent todoId: {}, nodeId: {} with info: {}", todoId, nodeId, todoInfo);
        validateTodoInfo(todoInfo);

        Optional<TodoNode> todoNodeOptional = todoRepo.get(todoId, nodeId);

        if (todoNodeOptional.isEmpty()) {
            log.error("Failed to create todo item: Parent node not found with todoId: {}, nodeId: {}", todoId, nodeId);
            throw new TodoException(format("TodoNode does not exist: %s, %s", todoId, nodeId));
        }

        TodoNode parentNode = todoNodeOptional.get();
        if (!LIST.equals(parentNode.getType())) {
            log.error("Failed to create todo item: Parent node is not a list type. todoId: {}, nodeId: {}, actual type: {}",
                todoId, nodeId, parentNode.getType());
            throw new TodoException(format("Parent node is not a list: %s, %s", todoId, nodeId));
        }

        TodoNode createdNode = todoRepo.createItem(todoId, nodeId, todoInfo);
        log.info("Successfully created new todo item with id: {} under parent: {}", createdNode.getId(), todoId);
        return createdNode;
    }

    @Override
    public TodoNode get(String todoId) throws TodoException {
        log.info("Retrieving todo with id: {}", todoId);
        Optional<TodoNode> todoNodeOptional = todoRepo.get(todoId);
        if (todoNodeOptional.isEmpty()) {
            log.error("Todo not found with id: {}", todoId);
            throw new TodoException(format("TodoNode does not exist: %s", todoId));
        }
        return todoNodeOptional.get();
    }

    private void validateTodoInfo(TodoInfo todoInfo) throws TodoValidationException {
        log.debug("Validating todo info: {}", todoInfo);
        try {
            todoValidator.validate(todoInfo);
        } catch (TodoValidationException e) {
            log.warn("Todo validation failed for info: {}, error: {}", todoInfo, e.getMessage());
            throw e;
        }
    }

    @Override
    public void display(String todoId) throws TodoException {
        log.info("Displaying todo with id: {}", todoId);
        TodoNode todoNode = get(todoId);
        // Assuming a display method exists in the TodoNode class
        todoDisplay.display(todoNode);
    }
}
