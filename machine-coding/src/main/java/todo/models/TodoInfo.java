package todo.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TodoInfo {

    private String name;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;

}
