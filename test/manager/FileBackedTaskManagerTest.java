package manager;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private FileBackedTaskManager fileBackedTaskManager;
    private final File file = File.createTempFile("kanban", "csv");

    public FileBackedTaskManagerTest() throws IOException {
        super(new FileBackedTaskManager(File.createTempFile("kanban", "csv").toString()));
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
    public void checkException() throws IOException {
        Task task1 = new Task(
                "Сделать зарядку",
                "Пробежать 30 минут",
                1,
                TaskStatus.NEW,
                TaskType.TASK,
                30,
                LocalDateTime.parse("2025-10-01T07:10:00")
        );

        Assertions.assertDoesNotThrow(() -> {fileBackedTaskManager.createTask(task1);});

        Assertions.assertThrows(ManagerSaveException.class, () -> new FileBackedTaskManager("/not/exist/path"));

    }
}
