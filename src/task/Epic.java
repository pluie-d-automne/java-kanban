package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Task> subtasks = new ArrayList<>();

    public Epic(String name, String description, int id, TaskStatus status) {
        super(name, description, id, status);
    }

    public List<Task> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Task> subtasks) {
        subtasks.removeIf(task -> task.equals(this));
        this.subtasks = subtasks;
    }
}
