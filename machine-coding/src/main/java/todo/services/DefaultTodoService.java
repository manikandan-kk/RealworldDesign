package todo.services;

import lombok.extern.slf4j.Slf4j;
import todo.display.TodoDisplay;
import todo.exceptions.TodoException;
import todo.exceptions.TodoValidationException;
import todo.locks.TodoLockHandler;
import todo.models.TodoInfo;
import todo.models.TodoNode;
import todo.repository.TodoRepo;
import todo.validator.TodoValidator;

import java.util.Optional;

import static java.lang.String.format;

@Slf4j
public class DefaultTodoService implements TodoService {

    private final TodoRepo todoRepo;
    private final TodoValidator todoValidator;
    private final TodoDisplay todoDisplay;
    private final TodoLockHandler todoLockHandler;

    public DefaultTodoService(TodoRepo todoRepo,
                              TodoValidator todoValidator,
                              TodoDisplay todoDisplay,
                              TodoLockHandler todoLockHandler) {
        this.todoRepo = todoRepo;
        this.todoValidator = todoValidator;
        this.todoDisplay = todoDisplay;
        this.todoLockHandler = todoLockHandler;
    }

    @Override
    public TodoNode createList(TodoInfo todoInfo) throws TodoValidationException {
        log.info("Creating new todo list with info: {}", todoInfo);
        todoValidator.validate(todoInfo);
        TodoNode node = todoRepo.createList(todoInfo);
        log.info("Successfully created new todo list with id: {}", node.getId());
        todoLockHandler.addLock(node.getId());
        return node;
    }

    @Override
    public TodoNode createList(String todoId, String nodeId, TodoInfo todoInfo)
            throws TodoValidationException, TodoException {
        boolean locked = false;
        try {
            locked = todoLockHandler.acquireWriteLock(todoId);
            throwIfLockNotAcquired(locked, todoId);
            log.info("Creating new todo list under parent todoId: {}, nodeId: {} with info: {}", todoId, nodeId, todoInfo);
            todoValidator.validateCreate(todoId, nodeId, todoInfo);

            TodoNode createdNode = todoRepo.createList(todoId, nodeId, todoInfo);
            log.info("Successfully created new todo list with id: {} under parent: {}", createdNode.getId(), todoId);
            return createdNode;
        } catch(InterruptedException e) {
            throw new TodoException(e.getMessage(), e);
        } finally {
            if (locked) {
                todoLockHandler.releaseWriteLock(todoId);
            }
        }
    }

    @Override
    public TodoNode createItem(TodoInfo todoInfo) throws TodoValidationException {
        log.info("Creating new todo item with info: {}", todoInfo);
        todoValidator.validate(todoInfo);
        TodoNode node = todoRepo.createItem(todoInfo);
        log.info("Successfully created new todo item with id: {}", node.getId());
        todoLockHandler.addLock(node.getId());
        return node;
    }

    @Override
    public TodoNode createItem(String todoId, String nodeId, TodoInfo todoInfo)
            throws TodoValidationException, TodoException {
        boolean locked = false;
        try {
            locked = todoLockHandler.acquireWriteLock(todoId);
            throwIfLockNotAcquired(locked, todoId);
            log.info("Creating new todo item under parent todoId: {}, nodeId: {} with info: {}", todoId, nodeId, todoInfo);
            todoValidator.validateCreate(todoId, nodeId, todoInfo);

            TodoNode createdNode = todoRepo.createItem(todoId, nodeId, todoInfo);
            log.info("Successfully created new todo item with id: {} under parent: {}", createdNode.getId(), todoId);
            return createdNode;
        } catch(InterruptedException e) {
            throw new TodoException(e.getMessage(), e);
        } finally {
            if (locked) {
                todoLockHandler.releaseWriteLock(todoId);
            }
        }
    }

    @Override
    public TodoNode get(String todoId) throws TodoException {
        boolean locked = false;
        try {
            locked = todoLockHandler.acquireReadLock(todoId);
            throwIfLockNotAcquired(locked, todoId);
            log.info("Retrieving todo with id: {}", todoId);
            Optional<TodoNode> todoNodeOptional = todoRepo.get(todoId);
            if (todoNodeOptional.isEmpty()) {
                log.error("Todo not found with id: {}", todoId);
                throw new TodoException(format("TodoNode does not exist: %s", todoId));
            }
            return todoNodeOptional.get();
        } catch(InterruptedException e) {
            throw new TodoException(e.getMessage(), e);
        } finally {
            if (locked) {
                todoLockHandler.releaseReadLock(todoId);
            }
        }
    }

    @Override
    public void display(String todoId) throws TodoException {
        boolean locked = false;
        try {
            locked = todoLockHandler.acquireReadLock(todoId);
            throwIfLockNotAcquired(locked, todoId);
            log.info("Displaying todo with id: {}", todoId);
            TodoNode todoNode = get(todoId);
            todoDisplay.display(todoNode);
        } catch(InterruptedException e) {
            throw new TodoException(e.getMessage(), e);
        } finally {
            if (locked) {
                todoLockHandler.releaseReadLock(todoId);
            }
        }
    }

    private void throwIfLockNotAcquired(boolean locked, String todoId) throws TodoException {
        if (!locked) {
            log.error("Failed to acquire lock for todoId: {}", todoId);
            throw new TodoException(format("Failed to acquire lock for todoId: %s", todoId));
        }
    }
}
