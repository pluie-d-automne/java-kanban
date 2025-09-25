package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void checkTaskEqualsById() {
        Task task1 = new Task(
                "Купить хлеб",
                "-",
                1,
                TaskStatus.IN_PROGRESS,
                TaskType.TASK
        );
        Task task2 = new Task(
                "Купить молоко",
                "123",
                1,
                TaskStatus.NEW,
                TaskType.TASK
        );
        Assertions.assertEquals(task1, task2);
    }
}
