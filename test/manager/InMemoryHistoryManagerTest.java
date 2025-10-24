package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;
import task.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void checkTaskOldVersionSaved() {
        Task task = new Task(
                "Сделать зарядку",
                "Пробежать 30 минут",
                1,
                TaskStatus.NEW,
                TaskType.TASK,
                30,
                LocalDateTime.parse("2025-10-01T07:00:00")
        );
        historyManager.add(task);
        task = new Task(
                "Испечь пирог",
                "-",
                2,
                TaskStatus.NEW,
                TaskType.TASK,
                120,
                LocalDateTime.parse("2025-10-01T10:00:00")
        );
        Assertions.assertNotEquals(historyManager.getHistory().getFirst(), task);
    }

    @Test
    public void checkTaskAddAndDelete() {
        Task task1 = new Task(
                "Сделать зарядку",
                "Пробежать 30 минут",
                1,
                TaskStatus.NEW,
                TaskType.TASK,
                30,
                LocalDateTime.parse("2025-10-01T07:00:00")
        );
        Task task2 = new Task(
                "Приготовить завтрак",
                "Овсянка",
                2,
                TaskStatus.NEW,
                TaskType.TASK,
                20,
                LocalDateTime.parse("2025-10-01T08:00:00")
        );
        Task task3 = new Task(
                "Позаниматься на практикуме",
                "Пройти 1 тему",
                3,
                TaskStatus.NEW,
                TaskType.TASK,
                60,
                LocalDateTime.parse("2025-10-01T10:00:00")
        );
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task2);
        historyManager.add(task1);
        List<Task> resultHistory = historyManager.getHistory();
        List<Task> targetHistory = new ArrayList<>(Arrays.asList(task3, task2, task1));
        boolean result = true;
        for (int i = 0; i < 3; i++) {
            if (!resultHistory.get(i).equals(targetHistory.get(i))) {
                result = false;
                break;
            }
        }
        Assertions.assertTrue(result);
    }

    @Test
    public void checkHistoryEmptyHistory() {
        List<Task> resultHistory = historyManager.getHistory();
        Assertions.assertEquals(0, resultHistory.size());
    }

    @Test
    public void checkHistoryStoresLastViewForDuplicates() {
        String oldDescription = "Пробежать 30 минут";
        String newDescription = "Пробежать 40 минут";
        Task task1 = new Task(
                "Сделать зарядку",
                oldDescription,
                1,
                TaskStatus.NEW,
                TaskType.TASK,
                30,
                LocalDateTime.parse("2025-10-01T07:00:00")
        );
        historyManager.add(task1);
        task1 = new Task(
                "Сделать зарядку",
                newDescription,
                1,
                TaskStatus.NEW,
                TaskType.TASK,
                40,
                LocalDateTime.parse("2025-10-01T10:00:00")
        );
        historyManager.add(task1);

        Assertions.assertEquals(1, historyManager.getHistory().size());
        Assertions.assertEquals(newDescription, historyManager.getHistory().getFirst().getDescription());

    }

    @Test
    public void checkTaskRemoveFromHistory(){
        Task task1 = new Task(
                "Сделать зарядку",
                "Пробежать 30 минут",
                1,
                TaskStatus.NEW,
                TaskType.TASK,
                30,
                LocalDateTime.parse("2025-10-01T07:00:00")
        );
        Task task2 = new Task(
                "Приготовить завтрак",
                "Овсянка",
                2,
                TaskStatus.NEW,
                TaskType.TASK,
                20,
                LocalDateTime.parse("2025-10-01T08:00:00")
        );
        Task task3 = new Task(
                "Позаниматься на практикуме",
                "Пройти 1 тему",
                3,
                TaskStatus.NEW,
                TaskType.TASK,
                60,
                LocalDateTime.parse("2025-10-01T10:00:00")
        );

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Удаление последней записи
        historyManager.remove(3);

        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertEquals(1, historyManager.getHistory().getFirst().getId());
        Assertions.assertEquals(2, historyManager.getHistory().getLast().getId());

        historyManager.add(task3);
        // Удаление первой записи
        historyManager.remove(1);

        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertEquals(2, historyManager.getHistory().getFirst().getId());
        Assertions.assertEquals(3, historyManager.getHistory().getLast().getId());

        // Удаление записи из середины (сейчас: 2, 3)
        historyManager.add(task1); // (2,3,1)
        historyManager.remove(3);  // (2,1)

        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertEquals(2, historyManager.getHistory().getFirst().getId());
        Assertions.assertEquals(1, historyManager.getHistory().getLast().getId());
    }
}
