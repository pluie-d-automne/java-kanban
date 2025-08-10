package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{
    private List<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description, int id, TaskStatus status) {
        super(name, description, id, status);
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Integer> subtasks) {
        this.subtasks = subtasks;
    }
}
