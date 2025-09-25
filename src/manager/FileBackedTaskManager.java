package manager;

import task.Epic;
import task.Subtask;
import task.Task;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final String filePath;

    public FileBackedTaskManager(String filePath) {
        this.filePath = filePath;
    }

    private void save() {

    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public int dropTaskById(int id) {
        int taskId = super.dropTaskById(id);
        save();
        return taskId;
    }

    @Override
    public int createTask(Task task) {
        int taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public int updateTask(int taskId, Task newTask) {
        int id = super.updateTask(taskId, newTask);
        save();
        return id;
    }

    @Override
    public void addSubtaskToEpic(Epic epic, Subtask subtask) {
        super.addSubtaskToEpic(epic, subtask);
        save();
    }

    @Override
    public void calculateEpicStatus(int epicId) {
        super.calculateEpicStatus(epicId);
        save();
    }
}
