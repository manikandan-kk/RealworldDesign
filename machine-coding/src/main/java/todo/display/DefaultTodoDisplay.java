package todo.display;

import todo.models.TodoInfo;
import todo.models.TodoList;
import todo.models.TodoNode;
import todo.models.TodoType;

import java.time.LocalDateTime;
import java.util.List;

import static todo.models.TodoType.LIST;

public class DefaultTodoDisplay implements TodoDisplay {

    private static final String TODO_DISPLAY_FORMAT = "%s | %s - %s";

    @Override
    public void display(TodoNode todoNode) {
        displayRecursively(todoNode, 0);
    }

    private void displayRecursively(TodoNode todoNode, int level) {
        int spaces = getSpaceIndentation(level);
        for(int i = 0; i < spaces; i++) {
            System.out.print(" ");
        }
        String displayString = getDisplayString(todoNode);
        System.out.println(displayString);
        TodoType todoType = todoNode.getType();
        if (LIST.equals(todoType)) {
            List<TodoNode> subTodos = ((TodoList) todoNode).getSubTodoNodes();
            for (TodoNode subTodo : subTodos) {
                displayRecursively(subTodo, level + 1);
            }
        }
    }

    private String getDisplayString(TodoNode todoNode) {
        TodoInfo todoInfo = todoNode.getTodoInfo();
        String name = todoInfo.getName();
        LocalDateTime todoStartTs = todoInfo.getStartTimestamp();
        LocalDateTime todoEndTs = todoInfo.getEndTimestamp();
        return String.format(TODO_DISPLAY_FORMAT, name, todoStartTs, todoEndTs);
    }

    private int getSpaceIndentation(int level) {
        return level * 4;
    }
}
