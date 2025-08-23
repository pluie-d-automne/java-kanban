package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epicTasks = new HashMap<>();
    private final Map<Integer, Subtask> subTasks = new HashMap<>();
    private int taskCounter = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int createTaskId() {
        taskCounter += 1;
        return taskCounter;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<Subtask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены");
    }

    @Override
    public void deleteAllEpics() {
        epicTasks.clear();
        subTasks.clear();
        System.out.println("Все эпики с подзадачами удалены");
    }

    @Override
    public void deleteAllSubtasks() {
        subTasks.clear();
        System.out.println("Все подзадачи удалены");
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        } else {
            System.out.println("Задачи с таким id не существует");
            return null;
        }
    }

    @Override
    public Task getEpicById(int id) {
        if (epicTasks.containsKey(id)) {
            Epic task = epicTasks.get(id);
            historyManager.add(task);
            return task;
        } else {
            System.out.println("Эпика с таким id не существует");
            return null;
        }
    }

    @Override
    public Task getSubtaskById(int id) {
        if (subTasks.containsKey(id)) {
            Subtask task = subTasks.get(id);
            historyManager.add(task);
            return task;
        } else {
            System.out.println("Подзадачи с таким id не существует");
            return null;
        }
    }

    @Override
    public void dropTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epicTasks.containsKey(id)) {
            List<Task> epicSubtasks = getEpicSubtasks(id);

            for (Task task : epicSubtasks) {
                Subtask subtask = (Subtask) task;
                subtask.setEpicId(null);
            }

            epicTasks.remove(id);
        } else if (subTasks.containsKey(id)) {
            Subtask subtask = subTasks.get(id);
            Integer epicId = subtask.getEpicId();
            Epic epic = epicTasks.get(epicId);

            if (epic != null) {
                List<Task> epicSubtasks = epic.getSubtasks();
                epicSubtasks.remove(subtask);
                epic.setSubtasks(epicSubtasks);
            }

            subTasks.remove(id);
        } else {
            System.out.println("Задачи с таким id не существует");
        }
    }

    public Integer getEpicIdByName(String name) {
        for (Task epic : epicTasks.values()) {
            if (epic.getName().equals(name)) {
                return epic.getId();
            }
        }
        System.out.println("Эпик с таким названием не найден");
        return null;
    }

    @Override
    public int createTask(Task task) {
        int taskId = task.getId();
        if (taskId > taskCounter) {
            taskCounter = taskId;
        }
        switch (task.getClass().getSimpleName()) {
            case "Epic" -> epicTasks.put(taskId, (Epic) task);
            case "Subtask" -> {
                Subtask subtask = (Subtask) task;
                subTasks.put(taskId, subtask);

                if (subtask.getEpicId() != null) {
                    Epic epic = epicTasks.get(subtask.getEpicId());
                    addSubtaskToEpic(epic, subtask);
                }
            }
            case "Task" -> tasks.put(taskId, task);
            default -> System.out.println("Вы пытаетесь записать задачу неизвестного типа");
        }
        return taskId;
    }

    @Override
    public int updateTask(int taskId, Task newTask) {
        switch(newTask.getClass().getSimpleName()) {
            case "Epic" -> {
                if (epicTasks.containsKey(taskId)) {
                    epicTasks.put(taskId, (Epic) newTask);
                } else {
                    System.out.println("Эпика с таким taskId не существует");
                }
            }
            case "Subtask" -> {
                if (subTasks.containsKey(taskId)) {
                    Subtask subtask = (Subtask) newTask;
                    subTasks.put(taskId, subtask);
                    Integer epicId = subtask.getEpicId();
                    if (epicId != null) {
                        Epic epic = epicTasks.get(epicId);
                        addSubtaskToEpic(epic, subtask);
                        calculateEpicStatus(epicId);
                    }
                } else {
                    System.out.println("Подзадачи с таким taskId не существует");
                }
            }
            case "Task" -> {
                if (tasks.containsKey(taskId)) {
                    tasks.put(taskId, newTask);
                } else {
                    System.out.println("Задачи с таким taskId не существует");
                }
            }
            default -> System.out.println("Вы пытаетесь записать задачу неизвестного типа");
        }
        return taskId;
    }

    @Override
    public List<Task> getEpicSubtasks(int epicId) {
        Epic epic = epicTasks.get(epicId);
        return epic.getSubtasks();
    }

    public void addSubtaskToEpic(Epic epic, Subtask subtask) {
        List<Task> epicSubtasks = epic.getSubtasks();
        epicSubtasks.add(subtask);
        epic.setSubtasks(epicSubtasks);
    }

    public void calculateEpicStatus(int epicId) {
        List<TaskStatus> taskStatuses = new ArrayList<>();
        List<Task> subtasks = getEpicSubtasks(epicId);
        Epic epic = epicTasks.get(epicId);
        TaskStatus status;

        for (Task subtask : subtasks) {
            status = subtask.getStatus();

            if (!taskStatuses.contains(status)) {
                taskStatuses.add(status);
            }
        }

        if (taskStatuses.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (taskStatuses.size() == 1) {
            if (taskStatuses.getFirst().equals(TaskStatus.NEW)) {
                epic.setStatus(TaskStatus.NEW);
            } else if (taskStatuses.getFirst().equals(TaskStatus.DONE)) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
