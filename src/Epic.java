import java.util.ArrayList;

public class Epic extends Task{
    ArrayList<Integer> subtaskIds;

    public Epic(String name, String description, int id, TaskStatus status) {
        super(name, description, id, status);
        this.subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void addSubtask(Integer subtaskId) {
        this.subtaskIds.add(subtaskId);
    }
}
