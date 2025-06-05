package todo.validator;

import lombok.extern.slf4j.Slf4j;
import todo.exceptions.TodoValidationException;
import todo.models.TodoInfo;
import todo.models.TodoNode;
import todo.repository.TodoRepo;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static todo.models.TodoType.LIST;

@Slf4j
public class DefaultTodoValidator implements TodoValidator {

    private final TodoRepo todoRepo;

    public DefaultTodoValidator(TodoRepo todoRepo) {
        this.todoRepo = todoRepo;
    }

    @Override
    public void validate(TodoInfo todoInfo) throws TodoValidationException {
        if (isNull(todoInfo)) {
            throw new TodoValidationException("TodoInfo cannot be null!");
        }
        log.debug("Validating todo info: {}", todoInfo);
        try {
            validateTodoDescription(todoInfo);
            validateTodoDuration(todoInfo);
        } catch (TodoValidationException e) {
            log.warn("Todo validation failed for info: {}, error: {}", todoInfo, e.getMessage());
            throw e;
        }
    }

    private void validateTodoDescription(TodoInfo todoInfo) throws TodoValidationException {
        String todoDesc = todoInfo.getName();
        if (isEmpty(todoDesc)) {
            throw new TodoValidationException("TodoName cannot be empty!");
        }
    }

    private void validateTodoDuration(TodoInfo todoInfo) throws TodoValidationException {
        LocalDateTime startDateTime = todoInfo.getStartTimestamp();
        LocalDateTime endDateTime = todoInfo.getEndTimestamp();
        if (startDateTime.isAfter(endDateTime)) {
            throw new TodoValidationException("Start timestamp cannot be after end timestamp");
        }
        LocalDateTime currentTimestamp = LocalDateTime.now();
        if (endDateTime.isBefore(currentTimestamp)) {
            throw new TodoValidationException("End timestamp cannot be before current timestamp");
        }
    }

    private void isValidTodoNodeHierarchy(String todoId, String nodeId) throws TodoValidationException {
        Optional<TodoNode> todoNodeOptional = todoRepo.get(todoId, nodeId);

        if (todoNodeOptional.isEmpty()) {
            log.error("Failed to create todo list: Parent node not found with todoId: {}, nodeId: {}", todoId, nodeId);
            throw new TodoValidationException(format("TodoNode does not exist: %s, %s", todoId, nodeId));
        }

        TodoNode parentNode = todoNodeOptional.get();
        if (!LIST.equals(parentNode.getType())) {
            log.error("Failed to create todo list: Parent node is not a list type. todoId: {}, nodeId: {}, actual type: {}",
                    todoId, nodeId, parentNode.getType());
            throw new TodoValidationException(format("Parent node is not a list: %s, %s", todoId, nodeId));
        }
    }

    private void isValidSubNodeDuration(String todoId, String nodeId, TodoInfo todoInfo) throws TodoValidationException {
        TodoNode parentNode = todoRepo.get(todoId, nodeId).get();
        TodoInfo parentNodeInfo = parentNode.getTodoInfo();

        if (todoInfo.getStartTimestamp().isBefore(parentNodeInfo.getStartTimestamp()) ||
            todoInfo.getEndTimestamp().isAfter(parentNodeInfo.getEndTimestamp())) {
            log.error("Sub-node duration is outside parent node's duration. " +
                      "Parent node: {}, Sub-node: {}", parentNodeInfo, todoInfo);
            throw new TodoValidationException("Sub-node duration must be within the parent node's duration");
        }
    }

    @Override
    public void validateCreate(String todoId, String nodeId, TodoInfo todoInfo) throws TodoValidationException {
        log.debug("Validating todo info for todoId: {}, nodeId: {}, info: {}", todoId, nodeId, todoInfo);
        validate(todoInfo);
        isValidTodoNodeHierarchy(todoId, nodeId);
        isValidSubNodeDuration(todoId, nodeId, todoInfo);
    }
}
