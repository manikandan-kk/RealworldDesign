package todo.validator;

import todo.exceptions.TodoValidationException;
import todo.models.TodoInfo;

public interface TodoValidator {

    void validate(TodoInfo todoInfo) throws TodoValidationException;

    void validateCreate(String todoId, String nodeId, TodoInfo todoInfo) throws TodoValidationException;

}
