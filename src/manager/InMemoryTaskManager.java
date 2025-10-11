package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epicTasks = new HashMap<>();
    protected final Map<Integer, Subtask> subTasks = new HashMap<>();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>();
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
    public Epic getEpicById(int id) {
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
    public Subtask getSubtaskById(int id) {
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
    public int dropTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epicTasks.containsKey(id)) {
            List<Subtask> epicSubtasks = getEpicSubtasks(id);

            for (Subtask subtask : epicSubtasks) {
                int subtaskId = subtask.getId();
                subTasks.remove(subtaskId);
                historyManager.remove(subtaskId);
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
            return -1;
        }
        historyManager.remove(id);
        return id;
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

                if (checkPeriodOverlap(subtask)) {
                    System.out.println("Вы пытаетесь добавить подзадачу на время, которое занято другой задачей.");
                    return taskId;
                }

                subTasks.put(taskId, subtask);

                if (subtask.getEpicId() != null) {
                    Epic epic = epicTasks.get(subtask.getEpicId());
                    addSubtaskToEpic(epic, subtask);
                }
            }
            case "Task" -> {
                if (checkPeriodOverlap(task)) {
                    System.out.println("Вы пытаетесь добавить задачу на время, которое занято другой задачей.");
                    return taskId;
                }
                tasks.put(taskId, task);
            }
            default -> System.out.println("Вы пытаетесь записать задачу неизвестного типа");
        }

        if (task.getStartTime() != null & !task.getClass().getSimpleName().equals("Epic")) {
            prioritizedTasks.add(task);
        }

        return taskId;
    }

    @Override
    public int updateTask(int taskId, Task newTask) {
        switch (newTask.getClass().getSimpleName()) {
            case "Epic" -> {
                if (epicTasks.containsKey(taskId)) {
                    epicTasks.put(taskId, (Epic) newTask);
                } else {
                    System.out.println("Эпика с таким taskId не существует");
                }
            }
            case "Subtask" -> {
                if (subTasks.containsKey(taskId)) {
                    Task oldTask = subTasks.get(taskId);
                    Subtask subtask = (Subtask) newTask;
                    subTasks.put(taskId, subtask);
                    Integer epicId = subtask.getEpicId();
                    prioritizedTasks.remove(oldTask);
                    prioritizedTasks.add(subtask);
                    if (epicId != null) {
                        Epic epic = epicTasks.get(epicId);
                        addSubtaskToEpic(epic, subtask);
                    }
                } else {
                    System.out.println("Подзадачи с таким taskId не существует");
                }
            }
            case "Task" -> {
                if (tasks.containsKey(taskId)) {
                    Task oldTask = tasks.get(taskId);
                    tasks.put(taskId, newTask);
                    prioritizedTasks.remove(oldTask);
                    prioritizedTasks.add(newTask);
                } else {
                    System.out.println("Задачи с таким taskId не существует");
                }
            }
            default -> System.out.println("Вы пытаетесь записать задачу неизвестного типа");
        }
        return taskId;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epicTasks.get(epicId);

        for (Task task : epic.getSubtasks()) {
            epicSubtasks.add((Subtask) task);
        }

        return epicSubtasks;
    }

    public void addSubtaskToEpic(Epic epic, Subtask subtask) {
        List<Task> epicSubtasks = epic.getSubtasks();
        int epicId = epic.getId();
        epicSubtasks.add(subtask);
        epic.setSubtasks(epicSubtasks);
        calculateEpicStatus(epicId);
    }

    public void calculateEpicStatus(int epicId) {
        List<TaskStatus> taskStatuses = new ArrayList<>();
        LocalDateTime endTime = null;
        LocalDateTime startTime = null;
        Duration duration = Duration.ofMinutes(0);
        List<Subtask> subtasks = getEpicSubtasks(epicId);
        Epic epic = epicTasks.get(epicId);
        TaskStatus status;

        for (Task subtask : subtasks) {
            status = subtask.getStatus();
            LocalDateTime subtaskStartTime = subtask.getStartTime();
            LocalDateTime subtaskEndTime = subtask.getEndTime();
            duration = duration.plus(subtask.getDuration());

            if (startTime==null) {
                startTime = subtaskStartTime;
                endTime = subtaskEndTime;
            } else {
                if (startTime.isAfter(subtaskStartTime)) {
                    startTime = subtaskStartTime;
                }
                if (endTime.isBefore(subtaskEndTime)) {
                    endTime = subtaskEndTime;
                }
            }

            if (!taskStatuses.contains(status)) {
                taskStatuses.add(status);
            }
        }

        epic.setDuration(duration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);

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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean checkTwoTasksOverlap (Task task1, Task task2) {
        if (task1.getStartTime()==null || task2.getStartTime()==null) {
            return true;
        }
        return !(task1.getStartTime().isAfter(task2.getEndTime()) || task1.getEndTime().isBefore(task2.getStartTime()));
    }
    public boolean checkPeriodOverlap(Task newTask) {
        long result = getPrioritizedTasks()
                .stream()
                .map(task -> checkTwoTasksOverlap(task, newTask))
                .filter(check -> check)
                .count();
        return result > 0;
    }
}
