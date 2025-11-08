package manager;

public final class Managers {
    public static TaskManager getDefault(String filePath) {
        return new FileBackedTaskManager(filePath);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getInmemoryTaskManager() {
        return new InMemoryTaskManager();
    }
}
