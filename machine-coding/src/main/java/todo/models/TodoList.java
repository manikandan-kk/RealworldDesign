package todo.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static todo.models.TodoType.LIST;

@Getter
public class TodoList extends TodoNode {

    public final TodoType type = LIST;

    public TodoList(TodoInfo todoInfo) {
        super(todoInfo);
        this.subTodoNodes = new ArrayList<>();
    }

    private List<TodoNode> subTodoNodes;

}
