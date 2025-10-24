package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Task> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name,
                String description,
                int id,
                TaskStatus status,
                TaskType taskType,
                int duration,
                LocalDateTime startTime) {
        super(name, description, id, status, taskType, duration, startTime);
        this.endTime = startTime.plus(Duration.ofMinutes(duration));
    }

    public List<Task> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Task> subtasks) {
        subtasks.removeIf(task -> task.equals(this));
        this.subtasks = subtasks;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
