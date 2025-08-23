package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int HISTORY_SIZE = 10;
    private final List<Task> taskHistory = new ArrayList<>(HISTORY_SIZE);

    @Override
    public void add(Task task) {
        if (taskHistory.size() == HISTORY_SIZE) {
            taskHistory.removeFirst();
        }
        taskHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}
