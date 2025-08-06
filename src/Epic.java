import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task{
    HashMap<Integer, Task> subtasks;

    public Epic(String name, String description, int id, TaskStatus status) {
        super(name, description, id, status);
        this.subtasks = new HashMap<>();
    }

    public HashMap<Integer, Task> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Integer subtaskId, Task subtask) {
        this.subtasks.put(subtaskId, subtask);
        calculateStatus();
    }

    public void removeSubtask(Integer subtaskId) {
        subtasks.remove(subtaskId);
        calculateStatus();
    }

    private void calculateStatus() {
        ArrayList<TaskStatus> taskStatuses = new ArrayList<>();
        for (Task task : subtasks.values()) {
            if (! taskStatuses.contains(task.getStatus())) {
                taskStatuses.add(task.getStatus());
            };
        }
        if (taskStatuses.isEmpty()) {
            setStatus(TaskStatus.NEW);
        } else if (taskStatuses.size()==1) {
            if (taskStatuses.getFirst().equals(TaskStatus.NEW)) {
                setStatus(TaskStatus.NEW);
            } else if (taskStatuses.getFirst().equals(TaskStatus.DONE)) {
                setStatus(TaskStatus.DONE);
            } else {
                setStatus(TaskStatus.IN_PROGRESS);
            }
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }

}
