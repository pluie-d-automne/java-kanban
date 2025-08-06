import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> standaloneTasks;
    HashMap<Integer, Task> epicTasks;
    HashMap<Integer,  HashMap<Integer, Task>> subTasks;
    public static int taskCounter;

    public TaskManager() {
        this.standaloneTasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.taskCounter = 0;
    }

    public HashMap<Integer, Task> getStandaloneTasks() {
        return standaloneTasks;
    }

    public void createTask(String name, String description) {
        Task newTask = new Task(name, description, taskCounter + 1, TaskStatus.NEW);
        taskCounter += 1;
        standaloneTasks.put(newTask.getId(), newTask);
    }

}
