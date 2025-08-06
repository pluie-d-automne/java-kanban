public class Task {
    private final String name;
    private final String description;
    private final int id;
    private final TaskStatus status;

    public Task( String name, String description, int id, TaskStatus status) {
        this.description = description;
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public int getId() {
        return id;
    }
}
