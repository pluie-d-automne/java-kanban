package task;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name,
                   String description,
                   int id,
                   TaskStatus status,
                   TaskType taskType,
                   int duration,
                   LocalDateTime startTime,
                   Integer epicId) {
        super(name, description, id, status, taskType, duration, startTime);
        this.epicId = epicId;
    }

    public void setEpicId(Integer epicId) {
        if (epicId == null || ! epicId.equals(this.getId())) {
            this.epicId = epicId;
        }
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        String csvString = super.toString();
        return csvString + epicId;
    }

}
