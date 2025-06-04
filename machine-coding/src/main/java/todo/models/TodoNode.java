package todo.models;

import lombok.Data;

import static todo.utils.TodoUtils.generateTodoId;

@Data
public abstract class TodoNode {

    private String id;
    private TodoInfo todoInfo;

    public abstract TodoType getType();

    public TodoNode(TodoInfo todoInfo) {
        this.id = generateTodoId();
        this.todoInfo = todoInfo;
    }

}
