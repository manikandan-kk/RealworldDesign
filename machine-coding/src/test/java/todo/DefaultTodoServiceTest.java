package todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import todo.display.DefaultTodoDisplay;
import todo.display.TodoDisplay;
import todo.exceptions.TodoException;
import todo.exceptions.TodoValidationException;
import todo.logic.TodoNodeIdentifier;
import todo.models.TodoInfo;
import todo.models.TodoNode;
import todo.repository.DefaultTodoRepo;
import todo.services.DefaultTodoService;
import todo.validator.DefaultTodoValidator;
import todo.validator.TodoValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static todo.constants.TestTodoConstants.*;
import static todo.constants.TestTodoConstants.TEST_TODO_NAME;

public class DefaultTodoServiceTest {

    private DefaultTodoService defaultTodoService;

    @BeforeEach
    public void setUp() {
        TodoNodeIdentifier todoNodeIdentifier = new TodoNodeIdentifier();
        DefaultTodoRepo defaultTodoRepo = new DefaultTodoRepo(todoNodeIdentifier);

        TodoValidator todoValidator = new DefaultTodoValidator();
        TodoDisplay todoDisplay = new DefaultTodoDisplay();

        defaultTodoService = new DefaultTodoService(defaultTodoRepo, todoValidator, todoDisplay);
    }

    @Test
    public void testAddTodoItem() throws TodoValidationException, TodoException {
        TodoInfo todoInfo = new TodoInfo(TEST_TODO_NAME, TEST_START_TS, TEST_END_TS);
        TodoNode todoNode = defaultTodoService.createItem(todoInfo);
        assertNotNull(defaultTodoService.get(todoNode.getId()));

        assertEquals(TEST_TODO_NAME, todoNode.getTodoInfo().getName());
        assertEquals(TEST_START_TS, todoNode.getTodoInfo().getStartTimestamp());
        assertEquals(TEST_END_TS, todoNode.getTodoInfo().getEndTimestamp());
    }
}
