package task;

import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subtasks;

    public Epic(String name, String description, int id, TaskStatus status) {
        super(name, description, id, status);
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Integer> subtasks) {
        this.subtasks = subtasks;
    }
}
