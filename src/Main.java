import manager.InMemoryTaskManager;
import manager.NotFoundException;
import manager.PeriodOverlapException;
import task.*;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        try {
            taskManager.createTask(
                    new Task(
                            "Сделать зарядку",
                            "Пробежать 30 минут",
                            taskManager.createTaskId(),
                            TaskStatus.NEW,
                            TaskType.TASK,
                            30,
                            LocalDateTime.parse("2025-10-01T07:10:00")
                    )
            );
        } catch (PeriodOverlapException e) {
            System.out.println(e.getMessage());
        }

        try {
            taskManager.createTask(
                    new Task(
                            "Сходить в магазн",
                            "Купить хлеб молоко шоколадку",
                            taskManager.createTaskId(),
                            TaskStatus.NEW,
                            TaskType.TASK,
                            30,
                            LocalDateTime.parse("2025-10-01T07:15:00")
                    )
            );
        } catch (PeriodOverlapException e) {
            System.out.println(e.getMessage());
        }

        try {
            taskManager.createTask(
                    new Epic(
                            "Собраться в отпуск",
                            "Спланировать и подготовить всё что нужно для хорошего отпуска",
                            taskManager.createTaskId(),
                            TaskStatus.NEW,
                            TaskType.EPIC,
                            0,
                            LocalDateTime.parse("1970-01-01T00:00:00"))
            );
        } catch (PeriodOverlapException e) {
            System.out.println(e.getMessage());
        }

        try {
            taskManager.createTask(
                    new Epic(
                            "Освоить Java",
                            "Научиться программировать на языке java",
                            taskManager.createTaskId(),
                            TaskStatus.NEW,
                            TaskType.EPIC,
                            0,
                            LocalDateTime.parse("1970-01-01T00:00:00"))
            );
        } catch (PeriodOverlapException e) {
            System.out.println(e.getMessage());
        }

        try {
            taskManager.createTask(
                    new Subtask(
                            "Купить билеты на самолёт",
                            "Выбрать оптимальный рейс и купить билеты",
                            taskManager.createTaskId(),
                            TaskStatus.NEW,
                            TaskType.SUBTASK,
                            20,
                            LocalDateTime.parse("2025-11-01T12:00:00"),
                            taskManager.getEpicIdByName("Собраться в отпуск")
                    )
            );
        } catch (PeriodOverlapException e) {
            System.out.println(e.getMessage());
        }

        try{
            taskManager.createTask(
                    new Subtask(
                            "Найти жильё",
                            "Выбрать подходящий отель и забронировать проживание",
                            taskManager.createTaskId(),
                            TaskStatus.NEW,
                            TaskType.SUBTASK,
                            60,
                            LocalDateTime.parse("2025-11-03T15:00:00"),
                            taskManager.getEpicIdByName("Собраться в отпуск")
                    )
            );
        } catch (PeriodOverlapException e) {
            System.out.println(e.getMessage());
        }

        try{
            taskManager.createTask(
                    new Subtask(
                            "Купить путеводитель",
                            "-",
                            taskManager.createTaskId(),
                            TaskStatus.NEW,
                            TaskType.SUBTASK,
                            15,
                            LocalDateTime.parse("2025-12-01T12:00:00"),
                            taskManager.getEpicIdByName("Собраться в отпуск")
                    )
            );
        }  catch (PeriodOverlapException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\nЗапросим задачи в разном порядке:");
        taskManager.getEpicById(3);
        taskManager.getEpicById(4);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        System.out.println("История:");
        System.out.println(taskManager.getHistory());

        taskManager.getEpicById(4);
        System.out.println("История:");
        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getSubtaskById(7);
        taskManager.getSubtaskById(5);
        System.out.println("История:");
        System.out.println(taskManager.getHistory());

        System.out.println("Удалим задачу 5");
        try{
            taskManager.deleteTask(5);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("История:");
        System.out.println(taskManager.getHistory());

        System.out.println("Удалим эпик 3 - с подзадачами");
        try{
            taskManager.deleteEpic(3);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("История:");
        System.out.println(taskManager.getHistory());

        System.out.println("Список задач по приоритету");
        System.out.println(taskManager.getPrioritizedTasks());
    }
}
