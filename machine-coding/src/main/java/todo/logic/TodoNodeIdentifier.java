package todo.logic;

import todo.models.TodoList;
import todo.models.TodoNode;
import todo.models.TodoType;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import static java.lang.String.format;

public class TodoNodeIdentifier {

    public Optional<TodoNode> identify(TodoNode node, String nodeId) {
        Queue<TodoNode> bfsQueue = new LinkedList<>();
        bfsQueue.add(node);
        while(!bfsQueue.isEmpty()) {

            Queue<TodoNode> nextLevelQueue = new LinkedList<>();

            while(!bfsQueue.isEmpty()) {
                TodoNode todoItem = bfsQueue.poll();
                TodoType todoType = todoItem.getType();
                if (todoItem.getId().equals(nodeId)) {
                    return Optional.of(todoItem);
                }
                switch(todoType) {
                    case LIST:
                        TodoList todoList = (TodoList) todoItem;
                        nextLevelQueue.addAll(todoList.getSubTodoNodes());
                        break;
                    default:
                        throw new IllegalStateException(format("Unexpected value: %s", todoType.name()));
                }
            }

            bfsQueue = nextLevelQueue;
        }

        return Optional.empty();
    }

}
