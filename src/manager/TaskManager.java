package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {
    int createTaskId();

    List<Task> getTasks();

    List<Epic> getEpicTasks();

    List<Subtask> getSubTasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    int dropTaskById(int id);

    int createTask(Task task);

    int updateTask(int taskId, Task newTask);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getPrioritizedTasks();

    Integer getEpicIdByName(String name);
}
