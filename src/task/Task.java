package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private final int id;
    private TaskStatus status;
    private final TaskType taskType;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name,
                String description,
                int id,
                TaskStatus status,
                TaskType taskType,
                long duration,
                LocalDateTime startTime) {
        this.description = description;
        this.id = id;
        this.name = name;
        this.status = status;
        this.taskType = taskType;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format(
                "%s,%s,%s,%s,%s,%s,%s,",
                id,
                taskType,
                name,
                status,
                description,
                duration.toMinutes(),
                startTime.toString()
        );
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

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }
}
