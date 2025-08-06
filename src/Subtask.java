public class Subtask extends Task{
    private Integer epicId;

    public Subtask(String name, String description, int id, TaskStatus status, Integer epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }
}
