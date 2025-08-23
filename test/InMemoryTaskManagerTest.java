import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

public class InMemoryTaskManagerTest {
    InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    void beforeEach() {
        inMemoryTaskManager = new InMemoryTaskManager();
    }


    @Test
    void checkTaskManagerAddsTasks() {
        int id = 1;
        Task newTask = new Task(
                "Сделать зарядку",
                "Пробежать 30 минут",
                id,
                TaskStatus.NEW
        );
        inMemoryTaskManager.createTask(newTask);
        Task foundTask = inMemoryTaskManager.getTaskById(id);
        Assertions.assertEquals(foundTask, newTask);
    }

    @Test
    void checkTaskManagerAddsEpics() {
        int id = 1;
        Epic newEpic = new Epic(
                "Спланировать отпуск",
                "Много разных дел",
                id,
                TaskStatus.NEW
        );
        inMemoryTaskManager.createTask(newEpic);
        Epic foundEpic = (Epic) inMemoryTaskManager.getEpicById(id);
        Assertions.assertEquals(foundEpic, newEpic);
    }

    @Test
    void checkTaskManagerAddsSubtasks() {
        int id = 1;
        Subtask newSubtask = new Subtask(
                "Спланировать отпуск",
                "Много разных дел",
                id,
                TaskStatus.NEW,
                null
        );
        inMemoryTaskManager.createTask(newSubtask);
        Subtask foundSubtask = (Subtask) inMemoryTaskManager.getSubtaskById(id);
        Assertions.assertEquals(foundSubtask, newSubtask);
    }

    @Test
    void checkTaskAddedUnchanged() {
        int id = 1;
        String origDesc = "Пробежать 30 минут";
        String origName = "Сделать зарядку";
        TaskStatus origStatus = TaskStatus.NEW;
        Task newTask = new Task(
                origName,
                origDesc,
                id,
                origStatus
        );
        inMemoryTaskManager.createTask(newTask);
        Task foundTask = inMemoryTaskManager.getTaskById(id);
        String foundDesc = foundTask.getDescription();
        String foundName = foundTask.getName();
        TaskStatus foundStatus = foundTask.getStatus();
        Assertions.assertTrue(origName.equals(foundName) & origDesc.equals(foundDesc) & origStatus.equals(foundStatus));
    }

    @Test
    void checkManualAngGeneratedTaskIdsNotConflict() {
        int manualId = 1;
        Task task1 = new Task(
                "Пробежать 30 минут",
                "Сделать зарядку",
                manualId,
                TaskStatus.NEW
        );
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(
                new Task(
                        "Сходить в магазн",
                        "Купить: хлеб, молоко, шоколадку",
                        inMemoryTaskManager.createTaskId(),
                        TaskStatus.NEW
                )
        );
        int taskCnt = inMemoryTaskManager.getTasks().size();
        Assertions.assertEquals(taskCnt, 2);
    }
}
