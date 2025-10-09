package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.time.LocalDateTime;
import java.util.List;

public class InMemoryTaskManagerTest {
    InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = new InMemoryTaskManager();
    }


    @Test
    public void checkTaskManagerAddsTasks() {
        int id = 1;
        Task newTask = new Task(
                "Сделать зарядку",
                "Пробежать 30 минут",
                id,
                TaskStatus.NEW,
                TaskType.TASK,
                30,
                LocalDateTime.parse("2025-10-01T07:00:00")
        );
        inMemoryTaskManager.createTask(newTask);
        Task foundTask = inMemoryTaskManager.getTaskById(id);
        Assertions.assertEquals(foundTask, newTask);
    }

    @Test
    public void checkTaskManagerAddsEpics() {
        int id = 1;
        Epic newEpic = new Epic(
                "Спланировать отпуск",
                "Много разных дел",
                id,
                TaskStatus.NEW,
                TaskType.EPIC,
                0,
                LocalDateTime.parse("1970-01-01T00:00:00")
        );
        inMemoryTaskManager.createTask(newEpic);
        Epic foundEpic = inMemoryTaskManager.getEpicById(id);
        Assertions.assertEquals(foundEpic, newEpic);
    }

    @Test
    public void checkTaskManagerAddsSubtasks() {
        int id = 1;
        Subtask newSubtask = new Subtask(
                "Купить билеты на самолёт",
                "Выбрать и купить билеты на самолёт",
                id,
                TaskStatus.NEW,
                TaskType.SUBTASK,
                20,
                LocalDateTime.parse("2025-10-01T10:00:00"),
                null
        );
        inMemoryTaskManager.createTask(newSubtask);
        Subtask foundSubtask = inMemoryTaskManager.getSubtaskById(id);
        Assertions.assertEquals(foundSubtask, newSubtask);
    }

    @Test
    public void checkTaskAddedUnchanged() {
        int id = 1;
        String origDesc = "Пробежать 30 минут";
        String origName = "Сделать зарядку";
        TaskStatus origStatus = TaskStatus.NEW;
        Task newTask = new Task(
                origName,
                origDesc,
                id,
                origStatus,
                TaskType.TASK,
                30,
                LocalDateTime.parse("2025-10-01T07:00:00")
        );
        inMemoryTaskManager.createTask(newTask);
        Task foundTask = inMemoryTaskManager.getTaskById(id);
        String foundDesc = foundTask.getDescription();
        String foundName = foundTask.getName();
        TaskStatus foundStatus = foundTask.getStatus();
        Assertions.assertTrue(origName.equals(foundName) & origDesc.equals(foundDesc) & origStatus.equals(foundStatus));
    }

    @Test
    public void checkManualAngGeneratedTaskIdsNotConflict() {
        int manualId = 1;
        Task task1 = new Task(
                "Пробежать 30 минут",
                "Сделать зарядку",
                manualId,
                TaskStatus.NEW,
                TaskType.TASK,
                30,
                LocalDateTime.parse("2025-10-01T07:00:00")
        );
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(
                new Task(
                        "Сходить в магазин",
                        "Купить хлеб молоко шоколадку",
                        inMemoryTaskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.TASK,
                        45,
                        LocalDateTime.parse("2025-10-10T07:00:00")
                )
        );
        int taskCnt = inMemoryTaskManager.getTasks().size();
        Assertions.assertEquals(2, taskCnt);
    }

    @Test
    public void checkDeletedSubtasksNotInEpic() {
        int epicId = inMemoryTaskManager.createTask(
                new Epic(
                        "Собраться в отпуск",
                        "Спланировать и подготовить всё что нужно для хорошего отпуска",
                        inMemoryTaskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.EPIC,
                        0,
                        LocalDateTime.parse("1970-01-01T07:00:00")
                )
        );

        int subtask1Id = inMemoryTaskManager.createTask(
                new Subtask(
                        "Купить билеты на самолёт",
                        "Выбрать оптимальный рейс и купить билеты",
                        inMemoryTaskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        30,
                        LocalDateTime.parse("2025-10-01T10:00:00"),
                        inMemoryTaskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        int subtask2Id = inMemoryTaskManager.createTask(
                new Subtask(
                        "Найти жильё",
                        "Выбрать подходящий отель и забронировать проживание",
                        inMemoryTaskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        60,
                        LocalDateTime.parse("2025-10-05T12:00:00"),
                        inMemoryTaskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        Task subtask1 = inMemoryTaskManager.getSubtaskById(subtask1Id);
        Task subtask2 = inMemoryTaskManager.getSubtaskById(subtask2Id);
        inMemoryTaskManager.dropTaskById(subtask1Id);
        Epic epic = inMemoryTaskManager.getEpicById(epicId);
        List<Task> subtasks = epic.getSubtasks();

        Assertions.assertEquals(1, subtasks.size());
        Assertions.assertFalse(subtasks.contains(subtask1));
        Assertions.assertTrue(subtasks.contains(subtask2));
    }
 }
