public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

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
                        "Выбрать оптимальный реёс и купить билеты",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        taskManager.getEpicIdByName("Собраться в отпуск")
                )
        );

        System.out.println("\nСписок отдельных задач:");
        System.out.println(taskManager.getStandaloneTasks());
        System.out.println("\nСписок эпиков:");
        System.out.println(taskManager.getEpicTasks());
        System.out.println(taskManager.getEpicSubtasks(3));
        System.out.println("\nСписок подзадач:");
        System.out.println(taskManager.getSubTasks());
    }
}
