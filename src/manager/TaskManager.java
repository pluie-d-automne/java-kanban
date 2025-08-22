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

    Task getEpicById(int id);

    Task getSubtaskById(int id);

    void dropTaskById(int id);

    int createTask(Task task);

    int updateTask(int taskId, Task newTask);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();
}
