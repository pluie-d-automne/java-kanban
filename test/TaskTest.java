import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

public class TaskTest {
    @Test
    public void checkTaskEqualsById() {
        Task task1 = new Task(
                "Купить хлеб",
                "-",
                1,
                TaskStatus.IN_PROGRESS
        );
        Task task2 = new Task(
                "Купить молоко",
                "123",
                1,
                TaskStatus.NEW
        );
        Assertions.assertEquals(task1, task2);
    }
}
