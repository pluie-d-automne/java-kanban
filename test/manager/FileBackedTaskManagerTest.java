package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
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
    public void checkTaskManagerCreatesAndLoadsEmptyFile() throws IOException {
        String fileData = Files.readString(file.toPath());
        Assertions.assertEquals(FileBackedTaskManager.CSV_HEAD, fileData);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasks = loadedManager.getTasks();
        List<Epic> epics = loadedManager.getEpicTasks();
        List<Subtask> subtasks = loadedManager.getSubTasks();
        Assertions.assertEquals(0,tasks.size());
        Assertions.assertEquals(0,epics.size());
        Assertions.assertEquals(0,subtasks.size());
    }

    @Test
    public void checkTaskManagerCreatesAndLoadsFileWithTasks() throws IOException {
        Task newTask = new Task(
                "Сделать зарядку",
                "Пробежать 30 минут",
                1,
                TaskStatus.NEW,
                TaskType.TASK,
                30,
                LocalDateTime.parse("2025-10-01T07:10:00")
        );
        Epic newEpic = new Epic(
                "Спланировать отпуск",
                "Много разных дел",
                2,
                TaskStatus.NEW,
                TaskType.EPIC,
                0,
                LocalDateTime.parse("1970-01-01T00:00:00")
        );
        Subtask newSubtask = new Subtask(
                "Купить билеты",
                "-",
                3,
                TaskStatus.NEW,
                TaskType.SUBTASK,
                30,
                LocalDateTime.parse("2025-10-01T12:00:00"),
                2
        );
        fileBackedTaskManager.createTask(newTask);
        fileBackedTaskManager.createTask(newEpic);
        fileBackedTaskManager.createTask(newSubtask);
        String testString = String.format(
                "%s%s\n%s\n%s\n",
                FileBackedTaskManager.CSV_HEAD,
                newTask,
                newEpic,
                newSubtask
        );
        String fileData = Files.readString(file.toPath());
        Assertions.assertEquals(testString, fileData);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Task task = loadedManager.getTaskById(1);
        Epic epic = loadedManager.getEpicById(2);
        Subtask subtask = loadedManager.getSubtaskById(3);
        Assertions.assertEquals(newTask, task);
        Assertions.assertEquals(newEpic, epic);
        Assertions.assertEquals(newSubtask, subtask);
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
                LocalDateTime.parse("2025-10-01T12:00:00")
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
                TaskType.EPIC,
                0,
                LocalDateTime.parse("1970-01-01T00:00:00")
        );
        fileBackedTaskManager.createTask(newEpic);
        Epic foundEpic = fileBackedTaskManager.getEpicById(id);
        Assertions.assertEquals(foundEpic, newEpic);
    }

    @Test
    public void checkTaskManagerAddsSubtasks() {
        int id = 1;
        Subtask newSubtask = new Subtask(
                "Купить билеты",
                "Выбрать и купить билеты",
                id,
                TaskStatus.NEW,
                TaskType.SUBTASK,
                30,
                LocalDateTime.parse("2025-10-01T12:00:00"),
                null
        );
        fileBackedTaskManager.createTask(newSubtask);
        Subtask foundSubtask = fileBackedTaskManager.getSubtaskById(id);
        Assertions.assertEquals(foundSubtask, newSubtask);
    }

    @Test
    public void checkTaskAddedUnchanged() {
        int id = 1;
        String origDesc = "Пробежать 30 минут";
        String origName = "Сделать зарядку";
        TaskStatus origStatus = TaskStatus.NEW;
        long origDuration = 30;
        LocalDateTime origStartTime = LocalDateTime.parse("2025-10-01T07:00:00");
        Task newTask = new Task(
                origName,
                origDesc,
                id,
                origStatus,
                TaskType.TASK,
                origDuration,
                origStartTime
        );
        fileBackedTaskManager.createTask(newTask);
        Task foundTask = fileBackedTaskManager.getTaskById(id);
        String foundDesc = foundTask.getDescription();
        String foundName = foundTask.getName();
        TaskStatus foundStatus = foundTask.getStatus();
        long foundDuration = foundTask.getDuration().toMinutes();
        LocalDateTime foundStartTime = foundTask.getStartTime();
        Assertions.assertTrue(
                origName.equals(foundName)
                        & origDesc.equals(foundDesc)
                        & origStatus.equals(foundStatus)
                        & origDuration==foundDuration
                        & origStartTime.equals(foundStartTime)
        );
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
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createTask(
                new Task(
                        "Сходить в магазн",
                        "Купить хлеб молоко шоколадку",
                        fileBackedTaskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.TASK,
                        25,
                        LocalDateTime.parse("2025-10-01T12:00:00")
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
                        TaskType.EPIC,
                        0,
                        LocalDateTime.parse("1970-01-01T00:00:00")
                )
        );

        int subtask1Id = fileBackedTaskManager.createTask(
                new Subtask(
                        "Купить билеты на самолёт",
                        "Выбрать оптимальный рейс и купить билеты",
                        fileBackedTaskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        45,
                        LocalDateTime.parse("2025-10-01T12:00:00"),
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
                        60,
                        LocalDateTime.parse("2025-10-01T13:00:00"),
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
