package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

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
                TaskStatus.NEW
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
                TaskStatus.NEW
        );
        inMemoryTaskManager.createTask(newEpic);
        Epic foundEpic = (Epic) inMemoryTaskManager.getEpicById(id);
        Assertions.assertEquals(foundEpic, newEpic);
    }

    @Test
    public void checkTaskManagerAddsSubtasks() {
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
    public void checkTaskAddedUnchanged() {
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
    public void checkManualAngGeneratedTaskIdsNotConflict() {
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
        Assertions.assertEquals(2, taskCnt);
    }

    @Test
    public void checkDeletedSubtasksNotInEpic() {
        int epicId = inMemoryTaskManager.createTask(
                new Epic(
                        "Собраться в отпуск",
                        "Спланировать и подготовить всё, что нужно для хорошего отпуска",
                        inMemoryTaskManager.createTaskId(),
                        TaskStatus.NEW)
        );

        int subtask1Id = inMemoryTaskManager.createTask(
                new Subtask(
                        "Купить билеты на самолёт",
                        "Выбрать оптимальный рейс и купить билеты",
                        inMemoryTaskManager.createTaskId(),
                        TaskStatus.NEW,
                        inMemoryTaskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        int subtask2Id = inMemoryTaskManager.createTask(
                new Subtask(
                        "Найти жильё",
                        "Выбрать подходящий отель и забронировать проживание",
                        inMemoryTaskManager.createTaskId(),
                        TaskStatus.NEW,
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
