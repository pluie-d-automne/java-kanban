package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager fileBackedTaskManager;
    File file = File.createTempFile("kanban", "csv");

    public FileBackedTaskManagerTest() throws IOException {
    }

    @BeforeEach
    public void beforeEach() {
        fileBackedTaskManager = new FileBackedTaskManager(file.toString());
    }


    @Test
    public void checkTaskManagerAddsTasks() {
        int id = 1;
        Task newTask = new Task(
                "Сделать зарядку",
                "Пробежать 30 минут",
                id,
                TaskStatus.NEW,
                TaskType.TASK
        );
        fileBackedTaskManager.createTask(newTask);
        Task foundTask = fileBackedTaskManager.getTaskById(id);
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
                TaskType.EPIC
        );
        fileBackedTaskManager.createTask(newEpic);
        Epic foundEpic = (Epic) fileBackedTaskManager.getEpicById(id);
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
                TaskType.SUBTASK,
                null
        );
        fileBackedTaskManager.createTask(newSubtask);
        Subtask foundSubtask = (Subtask) fileBackedTaskManager.getSubtaskById(id);
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
                TaskType.TASK
        );
        fileBackedTaskManager.createTask(newTask);
        Task foundTask = fileBackedTaskManager.getTaskById(id);
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
                TaskType.TASK
        );
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createTask(
                new Task(
                        "Сходить в магазн",
                        "Купить хлеб молоко шоколадку",
                        fileBackedTaskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.TASK
                )
        );
        int taskCnt = fileBackedTaskManager.getTasks().size();
        Assertions.assertEquals(2, taskCnt);
    }

    @Test
    public void checkDeletedSubtasksNotInEpic() {
        int epicId = fileBackedTaskManager.createTask(
                new Epic(
                        "Собраться в отпуск",
                        "Спланировать и подготовить всё, что нужно для хорошего отпуска",
                        fileBackedTaskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.EPIC)
        );

        int subtask1Id = fileBackedTaskManager.createTask(
                new Subtask(
                        "Купить билеты на самолёт",
                        "Выбрать оптимальный рейс и купить билеты",
                        fileBackedTaskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        fileBackedTaskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        int subtask2Id = fileBackedTaskManager.createTask(
                new Subtask(
                        "Найти жильё",
                        "Выбрать подходящий отель и забронировать проживание",
                        fileBackedTaskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        fileBackedTaskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        Task subtask1 = fileBackedTaskManager.getSubtaskById(subtask1Id);
        Task subtask2 = fileBackedTaskManager.getSubtaskById(subtask2Id);
        fileBackedTaskManager.dropTaskById(subtask1Id);
        Epic epic = fileBackedTaskManager.getEpicById(epicId);
        List<Task> subtasks = epic.getSubtasks();

        Assertions.assertEquals(1, subtasks.size());
        Assertions.assertFalse(subtasks.contains(subtask1));
        Assertions.assertTrue(subtasks.contains(subtask2));
    }
}
