package todo.validator;

import todo.exceptions.TodoValidationException;
import todo.models.TodoInfo;

public interface TodoValidator {

    void validate(TodoInfo todoInfo) throws TodoValidationException;

}
