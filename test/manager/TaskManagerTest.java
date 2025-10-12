package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.time.LocalDateTime;
import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
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
        taskManager.createTask(newTask);
        Task foundTask = taskManager.getTaskById(id);
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
        taskManager.createTask(newEpic);
        Epic foundEpic = taskManager.getEpicById(id);
        Assertions.assertEquals(foundEpic, newEpic);
    }

    @Test
    public void checkTaskManagerAddsSubtasks() {
        int epicId = taskManager.createTask(
                new Epic(
                        "Собраться в отпуск",
                        "Спланировать и подготовить всё что нужно для хорошего отпуска",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.EPIC,
                        0,
                        LocalDateTime.parse("1970-01-01T07:00:00")
                )
        );

        Subtask newSubtask = new Subtask(
                "Купить билеты на самолёт",
                "Выбрать и купить билеты на самолёт",
                taskManager.createTaskId(),
                TaskStatus.NEW,
                TaskType.SUBTASK,
                20,
                LocalDateTime.parse("2025-10-01T10:00:00"),
                epicId
        );
        int subtaskId = taskManager.createTask(newSubtask);
        Subtask foundSubtask = taskManager.getSubtaskById(subtaskId);
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
        taskManager.createTask(newTask);
        Task foundTask = taskManager.getTaskById(id);
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
        taskManager.createTask(task1);
        taskManager.createTask(
                new Task(
                        "Сходить в магазин",
                        "Купить хлеб молоко шоколадку",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.TASK,
                        45,
                        LocalDateTime.parse("2025-10-10T07:00:00")
                )
        );
        int taskCnt = taskManager.getTasks().size();
        Assertions.assertEquals(2, taskCnt);
    }

    @Test
    public void checkDeletedSubtasksNotInEpic() {
        int epicId = taskManager.createTask(
                new Epic(
                        "Собраться в отпуск",
                        "Спланировать и подготовить всё что нужно для хорошего отпуска",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.EPIC,
                        0,
                        LocalDateTime.parse("1970-01-01T07:00:00")
                )
        );

        int subtask1Id = taskManager.createTask(
                new Subtask(
                        "Купить билеты на самолёт",
                        "Выбрать оптимальный рейс и купить билеты",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        30,
                        LocalDateTime.parse("2025-10-01T10:00:00"),
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        int subtask2Id = taskManager.createTask(
                new Subtask(
                        "Найти жильё",
                        "Выбрать подходящий отель и забронировать проживание",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        60,
                        LocalDateTime.parse("2025-10-05T12:00:00"),
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        Task subtask1 = taskManager.getSubtaskById(subtask1Id);
        Task subtask2 = taskManager.getSubtaskById(subtask2Id);
        taskManager.dropTaskById(subtask1Id);
        Epic epic = taskManager.getEpicById(epicId);
        List<Task> subtasks = epic.getSubtasks();

        Assertions.assertEquals(1, subtasks.size());
        Assertions.assertFalse(subtasks.contains(subtask1));
        Assertions.assertTrue(subtasks.contains(subtask2));
    }

    @Test
    public void checkEpicStatus() {
        int epicId = taskManager.createTask(
                new Epic(
                        "Собраться в отпуск",
                        "Спланировать и подготовить всё что нужно для хорошего отпуска",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.EPIC,
                        0,
                        LocalDateTime.parse("1970-01-01T07:00:00")
                )
        );

        int subtask1Id = taskManager.createTask(
                new Subtask(
                        "Купить билеты на самолёт",
                        "Выбрать оптимальный рейс и купить билеты",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        30,
                        LocalDateTime.parse("2025-10-01T10:00:00"),
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        int subtask2Id = taskManager.createTask(
                new Subtask(
                        "Найти жильё",
                        "Выбрать подходящий отель и забронировать проживание",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        60,
                        LocalDateTime.parse("2025-10-05T12:00:00"),
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        Assertions.assertEquals(TaskStatus.NEW, taskManager.getEpicById(epicId).getStatus());

        taskManager.updateTask(
                subtask2Id,
                new Subtask(
                        "Найти жильё",
                        "Выбрать подходящий отель и забронировать проживание",
                        subtask2Id,
                        TaskStatus.DONE,
                        TaskType.SUBTASK,
                        60,
                        LocalDateTime.parse("2025-10-05T12:00:00"),
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epicId).getStatus());

        taskManager.updateTask(
                subtask1Id,
                new Subtask(
                        "Купить билеты на самолёт",
                        "Выбрать оптимальный рейс и купить билеты",
                        subtask1Id,
                        TaskStatus.DONE,
                        TaskType.SUBTASK,
                        30,
                        LocalDateTime.parse("2025-10-01T10:00:00"),
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getEpicById(epicId).getStatus());

        taskManager.updateTask(
                subtask2Id,
                new Subtask(
                        "Найти жильё",
                        "Выбрать подходящий отель и забронировать проживание",
                        subtask2Id,
                        TaskStatus.IN_PROGRESS,
                        TaskType.SUBTASK,
                        60,
                        LocalDateTime.parse("2025-10-05T12:00:00"),
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        taskManager.updateTask(
                subtask1Id,
                new Subtask(
                        "Купить билеты на самолёт",
                        "Выбрать оптимальный рейс и купить билеты",
                        subtask1Id,
                        TaskStatus.IN_PROGRESS,
                        TaskType.SUBTASK,
                        30,
                        LocalDateTime.parse("2025-10-01T10:00:00"),
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epicId).getStatus());
    }

    @Test
    public void checkAllSubtasksHaveEpic() {
        int epicId = taskManager.createTask(
                new Epic(
                        "Собраться в отпуск",
                        "Спланировать и подготовить всё что нужно для хорошего отпуска",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.EPIC,
                        0,
                        LocalDateTime.parse("1970-01-01T07:00:00")
                )
        );

        int subtask1Id = taskManager.createTask(
                new Subtask(
                        "Купить билеты на самолёт",
                        "Выбрать оптимальный рейс и купить билеты",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        30,
                        LocalDateTime.parse("2025-10-01T10:00:00"),
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        int subtask2Id = taskManager.createTask(
                new Subtask(
                        "Найти жильё",
                        "Выбрать подходящий отель и забронировать проживание",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        60,
                        LocalDateTime.parse("2025-10-05T12:00:00"),
                        null
                )
        );

        for (Subtask subtask : taskManager.getSubTasks()) {
            Assertions.assertNotNull(subtask.getEpicId());
        }

    }

    @Test
    public void checkPeriodOverlapsDefinedCorrectly() {
        Task task1 = new Task(
                "Сделать зарядку",
                "Пробежать 30 минут",
                taskManager.createTaskId(),
                TaskStatus.NEW,
                TaskType.TASK,
                30,
                LocalDateTime.parse("2025-10-01T07:00:00")
        );
        Task task2 = new Task(
                "Сходить за покупками",
                "Купить хлеб овощи и фрукты",
                taskManager.createTaskId(),
                TaskStatus.NEW,
                TaskType.TASK,
                30,
                LocalDateTime.parse("2025-10-01T07:31:00")
        );

        Assertions.assertFalse(taskManager.checkTwoTasksOverlap(task1, task2));

        task1 = new Task(
                "Сделать зарядку",
                "Пробежать 30 минут",
                taskManager.createTaskId(),
                TaskStatus.NEW,
                TaskType.TASK,
                30,
                LocalDateTime.parse("2025-10-01T07:10:00")
        );

        Assertions.assertTrue(taskManager.checkTwoTasksOverlap(task1, task2));

        task2 = new Task(
                "Сходить за покупками",
                "Купить хлеб овощи и фрукты",
                taskManager.createTaskId(),
                TaskStatus.NEW,
                TaskType.TASK,
                15,
                LocalDateTime.parse("2025-10-01T07:11:00")
        );

        Assertions.assertTrue(taskManager.checkTwoTasksOverlap(task1, task2));
    }
}
