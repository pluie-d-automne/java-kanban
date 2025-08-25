import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

public class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    public void checkTaskOldVersionSaved() {
        Task task = new Task(
                "Сделать зарядку",
                "Пробежать 30 минут",
                1,
                TaskStatus.NEW
        );
        historyManager.add(task);
        task = new Task(
                "Испечь пирог",
                "-",
                2,
                TaskStatus.NEW
        );
        Assertions.assertNotEquals(historyManager.getHistory().getFirst(), task);
    }
}
