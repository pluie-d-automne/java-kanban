import manager.InMemoryTaskManager;
import task.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        taskManager.createTask(
                new Task(
                        "Сделать зарядку",
                        "Пробежать 30 минут",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.TASK
                )
        );
        taskManager.createTask(
                new Task(
                        "Сходить в магазн",
                        "Купить хлеб молоко шоколадку",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.TASK
                )
        );
        taskManager.createTask(
                new Epic(
                        "Собраться в отпуск",
                        "Спланировать и подготовить всё что нужно для хорошего отпуска",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.EPIC)
        );
        taskManager.createTask(
                new Epic(
                        "Освоить Java",
                        "Научиться программировать на языке java",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.EPIC)
        );
        taskManager.createTask(
                new Subtask(
                        "Купить билеты на самолёт",
                        "Выбрать оптимальный рейс и купить билеты",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );
        taskManager.createTask(
                new Subtask(
                        "Найти жильё",
                        "Выбрать подходящий отель и забронировать проживание",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        taskManager.createTask(
                new Subtask(
                        "Купить путеводитель",
                        "-",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );
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
        taskManager.dropTaskById(5);
        System.out.println("История:");
        System.out.println(taskManager.getHistory());

        System.out.println("Удалим эпик 3 - с подзадачами");
        taskManager.dropTaskById(3);
        System.out.println("История:");
        System.out.println(taskManager.getHistory());
    }
}
