package todo.validator;

import org.apache.commons.lang3.StringUtils;
import todo.exceptions.TodoValidationException;
import todo.models.TodoInfo;

import java.time.LocalDateTime;

public class DefaultTodoValidator implements TodoValidator {

    @Override
    public void validate(TodoInfo todoInfo) throws TodoValidationException {
        validateTodoDescription(todoInfo);
        validateTodoDuration(todoInfo);
    }

    private void validateTodoDescription(TodoInfo todoInfo) throws TodoValidationException {
        String todoDesc = todoInfo.getName();
        if (StringUtils.isEmpty(todoDesc)) {
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
}
