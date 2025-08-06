public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.createTask("Сделать зарядку", "Пробежать 30 минут");
        taskManager.createTask("Сходить в магазн", "Купить: хлеб, молоко, шоколадку");

        System.out.println(taskManager.getStandaloneTasks());
    }
}
