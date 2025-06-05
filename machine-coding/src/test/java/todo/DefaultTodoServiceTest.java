package todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import todo.display.DefaultTodoDisplay;
import todo.display.TodoDisplay;
import todo.exceptions.TodoException;
import todo.exceptions.TodoValidationException;
import todo.locks.TodoLockHandler;
import todo.logic.TodoNodeIdentifier;
import todo.models.TodoInfo;
import todo.models.TodoNode;
import todo.repository.DefaultTodoRepo;
import todo.repository.TodoRepo;
import todo.services.DefaultTodoService;
import todo.validator.DefaultTodoValidator;
import todo.validator.TodoValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static todo.constants.TestTodoConstants.*;

class DefaultTodoServiceTest {

    private DefaultTodoService defaultTodoService;
    private TodoRepo todoRepo;
    private TodoValidator todoValidator;
    private TodoDisplay todoDisplay;
    private TodoLockHandler todoLockHandler;

    @BeforeEach
    void setUp() {
        TodoNodeIdentifier todoNodeIdentifier = new TodoNodeIdentifier();
        todoRepo = new DefaultTodoRepo(todoNodeIdentifier);
        todoValidator = spy(new DefaultTodoValidator(todoRepo));
        todoDisplay = new DefaultTodoDisplay();
        todoLockHandler = spy(new TodoLockHandler());

        defaultTodoService = new DefaultTodoService(todoRepo, todoValidator, todoDisplay, todoLockHandler);
    }

    @Test
    void createList_WithValidInfo_ShouldCreateTodoList() throws TodoValidationException {
        TodoInfo todoInfo = new TodoInfo(TEST_TODO_NAME, TEST_START_TS, TEST_END_TS);
        TodoNode todoNode = defaultTodoService.createList(todoInfo);

        assertNotNull(todoNode);
        assertEquals(TEST_TODO_NAME, todoNode.getTodoInfo().getName());
        verify(todoValidator).validate(todoInfo);
        verify(todoLockHandler).addLock(todoNode.getId());
    }

    @Test
    void createList_WithInvalidInfo_ShouldThrowValidationException() {
        TodoInfo invalidInfo = new TodoInfo(TEST_TODO_NAME, TEST_END_TS, TEST_START_TS); // end before start
        assertThrows(TodoValidationException.class, () -> defaultTodoService.createList(invalidInfo));
    }

    @Test
    void createList_UnderParent_WithValidInfo_ShouldCreateNestedList()
            throws TodoValidationException, TodoException, InterruptedException {
        // Create parent list first
        TodoInfo parentInfo = new TodoInfo("Parent", TEST_START_TS, TEST_END_TS);
        TodoNode parentNode = defaultTodoService.createList(parentInfo);

        // Create child list
        TodoInfo childInfo = new TodoInfo("Child", TEST_START_TS, TEST_END_TS);
        when(todoLockHandler.acquireWriteLock(parentNode.getId())).thenReturn(true);

        TodoNode childNode = defaultTodoService.createList(parentNode.getId(), parentNode.getId(), childInfo);

        assertNotNull(childNode);
        assertEquals("Child", childNode.getTodoInfo().getName());
        verify(todoLockHandler).acquireWriteLock(parentNode.getId());
        verify(todoLockHandler).releaseWriteLock(parentNode.getId());
    }

    @Test
    void createItem_WithValidInfo_ShouldCreateTodoItem() throws TodoValidationException {
        TodoInfo todoInfo = new TodoInfo(TEST_TODO_NAME, TEST_START_TS, TEST_END_TS);
        TodoNode todoNode = defaultTodoService.createItem(todoInfo);

        assertNotNull(todoNode);
        assertEquals(TEST_TODO_NAME, todoNode.getTodoInfo().getName());
        verify(todoValidator).validate(todoInfo);
        verify(todoLockHandler).addLock(todoNode.getId());
    }

    @Test
    void createItem_UnderParent_WithValidInfo_ShouldCreateNestedItem()
            throws TodoValidationException, TodoException, InterruptedException {
        // Create parent list first
        TodoInfo parentInfo = new TodoInfo("Parent", TEST_START_TS, TEST_END_TS);
        TodoNode parentNode = defaultTodoService.createList(parentInfo);

        // Create child item
        TodoInfo childInfo = new TodoInfo("Child Item", TEST_START_TS, TEST_END_TS);
        when(todoLockHandler.acquireWriteLock(parentNode.getId())).thenReturn(true);

        TodoNode childNode = defaultTodoService.createItem(parentNode.getId(), parentNode.getId(), childInfo);

        assertNotNull(childNode);
        assertEquals("Child Item", childNode.getTodoInfo().getName());
        verify(todoLockHandler).acquireWriteLock(parentNode.getId());
        verify(todoLockHandler).releaseWriteLock(parentNode.getId());
    }

    @Test
    void createItem_UnderParent_WhenLockFails_ShouldThrowException()
            throws InterruptedException, TodoValidationException {
        TodoInfo parentInfo = new TodoInfo("Parent", TEST_START_TS, TEST_END_TS);
        TodoNode parentNode = defaultTodoService.createList(parentInfo);

        TodoInfo childInfo = new TodoInfo("Child", TEST_START_TS, TEST_END_TS);
        when(todoLockHandler.acquireWriteLock(parentNode.getId())).thenReturn(false);

        assertThrows(TodoException.class,
                () -> defaultTodoService.createItem(parentNode.getId(), parentNode.getId(), childInfo));
    }

    @Test
    void get_ExistingTodo_ShouldReturnTodo() throws TodoValidationException, TodoException, InterruptedException {
        // Create a todo first
        TodoInfo todoInfo = new TodoInfo(TEST_TODO_NAME, TEST_START_TS, TEST_END_TS);
        TodoNode createdNode = defaultTodoService.createList(todoInfo);

        when(todoLockHandler.acquireReadLock(createdNode.getId())).thenReturn(true);

        TodoNode retrievedNode = defaultTodoService.get(createdNode.getId());

        assertNotNull(retrievedNode);
        assertEquals(createdNode.getId(), retrievedNode.getId());
        verify(todoLockHandler).acquireReadLock(createdNode.getId());
        verify(todoLockHandler).releaseReadLock(createdNode.getId());
    }
}
