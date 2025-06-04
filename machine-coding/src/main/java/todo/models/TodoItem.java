package todo.models;

import lombok.Getter;

import static todo.models.TodoType.ITEM;

@Getter
public class TodoItem extends TodoNode {

    public final TodoType type = ITEM;

    public TodoItem(TodoInfo todoInfo) {
        super(todoInfo);
    }

}
