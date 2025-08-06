import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> standaloneTasks;
    HashMap<Integer, Task> epicTasks;
    HashMap<Integer, Task> subTasks;
    public int taskCounter;

    public TaskManager() {
        this.standaloneTasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.taskCounter = 0;
    }

    public int createTaskId() {
        taskCounter += 1;
        return taskCounter;
    }

    public HashMap<Integer, Task> getStandaloneTasks() {
        return standaloneTasks;
    }

    public HashMap<Integer, Task> getEpicTasks() {
        return epicTasks;
    }

    public HashMap<Integer, Task> getSubTasks() {
        return subTasks;
    }

    public Integer getEpicIdByName(String name) {
        for (Task epic : epicTasks.values()) {
            if (epic.getName().equals(name)) {
                return epic.getId();
            }
        }
        System.out.println("Эпик с таким названием не найден");
        return null;
    }

    public void createTask(Task task) {
        switch (task.getClass().getSimpleName()) {
            case "Epic" -> epicTasks.put(task.getId(), task);
            case "Subtask" -> {
                int taskId = task.getId();
                subTasks.put(task.getId(), task);
                Subtask subtask = (Subtask) subTasks.get(taskId);
                if (subtask.getEpicId()!=null) {
                    Epic epic = (Epic) epicTasks.get(subtask.getEpicId());
                    epic.addSubtask(taskId);
                }
            }
            default -> standaloneTasks.put(task.getId(), task);
        }
    }

    public ArrayList<Integer> getEpicSubtasks(int epicId){
        Epic epic = (Epic) epicTasks.get(epicId);
        return epic.getSubtaskIds();
    }

}
