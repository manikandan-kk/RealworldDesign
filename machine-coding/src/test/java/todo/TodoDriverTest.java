package todo;

import todo.display.DefaultTodoDisplay;
import todo.display.TodoDisplay;
import todo.exceptions.TodoException;
import todo.exceptions.TodoValidationException;
import todo.locks.TodoLockHandler;
import todo.logic.TodoNodeIdentifier;
import todo.models.TodoInfo;
import todo.models.TodoNode;
import todo.repository.DefaultTodoRepo;
import todo.services.DefaultTodoService;
import todo.validator.DefaultTodoValidator;
import todo.validator.TodoValidator;

import static todo.constants.TestTodoConstants.*;

public class TodoDriverTest {

    private DefaultTodoService defaultTodoService;

    public TodoDriverTest() {
        TodoNodeIdentifier todoNodeIdentifier = new TodoNodeIdentifier();
        DefaultTodoRepo defaultTodoRepo = new DefaultTodoRepo(todoNodeIdentifier);

        TodoValidator todoValidator = new DefaultTodoValidator(defaultTodoRepo);
        TodoDisplay todoDisplay = new DefaultTodoDisplay();

        TodoLockHandler todoLockHandler = new TodoLockHandler();

        defaultTodoService = new DefaultTodoService(defaultTodoRepo, todoValidator, todoDisplay, todoLockHandler);
    }

    public static void main(String[] args) throws TodoValidationException, TodoException {
        TodoDriverTest todoDriverTest = new TodoDriverTest();
        todoDriverTest.test();
    }

    private void test() throws TodoValidationException, TodoException {
        TodoInfo rootNodeInfo = new TodoInfo(TEST_TODO_NAME, TEST_START_TS, TEST_END_TS);
        TodoNode rootTodoNode = defaultTodoService.createList(rootNodeInfo);

        TodoInfo l1_1Info = new TodoInfo(TEST_TODO_LIST_L1_1_NAME, TEST_START_TS, TEST_END_TS);
        TodoNode l1_1Node = defaultTodoService.createList(rootTodoNode.getId(), rootTodoNode.getId(), l1_1Info);
        TodoInfo l1_2Info = new TodoInfo(TEST_TODO_LIST_L1_2_NAME, TEST_START_TS, TEST_END_TS);
        TodoNode l1_2Node = defaultTodoService.createList(rootTodoNode.getId(), rootTodoNode.getId(), l1_2Info);
        TodoInfo l1_3Info = new TodoInfo(TEST_TODO_LIST_L1_3_NAME, TEST_START_TS, TEST_END_TS);
        TodoNode l1_3Node = defaultTodoService.createItem(rootTodoNode.getId(), rootTodoNode.getId(), l1_3Info);

        TodoInfo l2_1_1Info = new TodoInfo(TEST_TODO_LIST_L2_1_1_NAME, TEST_START_TS, TEST_END_TS);
        TodoNode l2_1_1Node = defaultTodoService.createList(rootTodoNode.getId(), l1_1Node.getId(), l2_1_1Info);
        TodoInfo l2_1_2Info = new TodoInfo(TEST_TODO_LIST_L2_1_2_NAME, TEST_START_TS, TEST_END_TS);
        TodoNode l2_1_2Node = defaultTodoService.createItem(rootTodoNode.getId(), l1_1Node.getId(), l2_1_2Info);
        TodoInfo l2_2_1Info = new TodoInfo(TEST_TODO_LIST_L2_2_1_NAME, TEST_START_TS, TEST_END_TS);
        TodoNode l2_2_1Node = defaultTodoService.createList(rootTodoNode.getId(), l1_2Node.getId(), l2_2_1Info);
        TodoInfo l2_2_2Info = new TodoInfo(TEST_TODO_LIST_L2_2_2_NAME, TEST_START_TS, TEST_END_TS);
        TodoNode l2_2_2Node = defaultTodoService.createItem(rootTodoNode.getId(), l1_2Node.getId(), l2_2_2Info);

        defaultTodoService.display(rootTodoNode.getId());
    }
}

