package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class TaskTest {
    @Test
    public void checkTaskEqualsById() {
        Task task1 = new Task(
                "Купить хлеб",
                "-",
                1,
                TaskStatus.IN_PROGRESS,
                TaskType.TASK,
                20,
                LocalDateTime.parse("2025-12-12T09:00:00")
        );
        Task task2 = new Task(
                "Купить молоко",
                "123",
                1,
                TaskStatus.NEW,
                TaskType.TASK,
                10,
                LocalDateTime.parse("2025-12-12T10:00:00")
        );
        Assertions.assertEquals(task1, task2);
    }
}
