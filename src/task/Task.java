package task;

import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private final int id;
    private TaskStatus status;
    private final TaskType taskType;

    public Task(String name, String description, int id, TaskStatus status, TaskType taskType) {
        this.description = description;
        this.id = id;
        this.name = name;
        this.status = status;
        this.taskType = taskType;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,", id, taskType, name, status, description);
    }

    public String getName() {
        return name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
