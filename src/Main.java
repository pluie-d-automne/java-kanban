import manager.InMemoryTaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        taskManager.createTask(
                new Task(
                        "Сделать зарядку",
                        "Пробежать 30 минут",
                        taskManager.createTaskId(),
                        TaskStatus.NEW
                )
        );
        taskManager.createTask(
                new Task(
                        "Сходить в магазн",
                        "Купить: хлеб, молоко, шоколадку",
                        taskManager.createTaskId(),
                        TaskStatus.NEW
                )
        );
        taskManager.createTask(
                new Epic(
                        "Собраться в отпуск",
                        "Спланировать и подготовить всё, что нужно для хорошего отпуска",
                        taskManager.createTaskId(),
                        TaskStatus.NEW)
        );
        taskManager.createTask(
                new Epic(
                        "Освоить Java",
                        "Научиться программировать на языке java",
                        taskManager.createTaskId(),
                        TaskStatus.NEW)
        );
        taskManager.createTask(
                new Subtask(
                        "Купить билеты на самолёт",
                        "Выбрать оптимальный рейс и купить билеты",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );
        taskManager.createTask(
                new Subtask(
                        "Найти жильё",
                        "Выбрать подходящий отель и забронировать проживание",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        taskManager.createTask(
                new Subtask(
                        "Сделать дамашнее задание",
                        "Сделать домашнее задание по текущему спринту",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        taskManager.getEpicIdByName("Освоить Java")
                )
        );

        System.out.println("\nСписок отдельных задач:");
        System.out.println(taskManager.getTasks());
        System.out.println("\nСписок эпиков:");
        System.out.println(taskManager.getEpicTasks());
        System.out.println("\nСписок подзадач:");
        System.out.println(taskManager.getSubTasks());
        System.out.println("\nНайдём эпик по id = 3:");
        System.out.println(taskManager.getEpicById(3));
        System.out.println("И выведем его подзадачи:");
        System.out.println(taskManager.getEpicSubtasks(3));
        System.out.println("\nОбновим статусы подзадач");
        taskManager.updateTask(
                7,
                new Subtask(
                        "Сделать дамашнее задание",
                        "Сделать домашнее задание по текущему спринту",
                        7,
                        TaskStatus.DONE,
                        taskManager.getEpicIdByName("Освоить Java")
                )
        );
        taskManager.updateTask(
                6,
                new Subtask(
                        "Найти жильё",
                        "Выбрать подходящий отель и забронировать проживание",
                        6,
                        TaskStatus.IN_PROGRESS,
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );
        System.out.println("\nСписок эпиков:");
        System.out.println(taskManager.getEpicTasks());
        System.out.println("\nСписок подзадач:");
        System.out.println(taskManager.getSubTasks());

        System.out.println("\nУдалим задачу, подзадачу и эпик");
        taskManager.dropTaskById(2);
        taskManager.dropTaskById(4);
        taskManager.dropTaskById(6);

        System.out.println("\nСписок отдельных задач:");
        System.out.println(taskManager.getTasks());
        System.out.println("\nСписок эпиков:");
        System.out.println(taskManager.getEpicTasks());
        System.out.println("\nСписок подзадач:");
        System.out.println(taskManager.getSubTasks());
    }
}
