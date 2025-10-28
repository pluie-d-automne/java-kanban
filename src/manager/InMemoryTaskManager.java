package manager;

import exceptions.NotFoundException;
import exceptions.PeriodOverlapException;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
            throw new NotFoundException("Задачи с таким id не существует");
        }
    }

    @Override
    public Epic getEpicById(int id) {
        if (epicTasks.containsKey(id)) {
            Epic task = epicTasks.get(id);
            historyManager.add(task);
            return task;
        } else {
            throw new NotFoundException("Эпика с таким id не существует");
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subTasks.containsKey(id)) {
            Subtask task = subTasks.get(id);
            historyManager.add(task);
            return task;
        } else {
            throw new NotFoundException("Подзадачи с таким id не существует");
        }
    }

    @Override
    public int deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
            return id;
        } else {
            throw new NotFoundException("Задачи с таким id не существует");
        }
    }

    @Override
    public int deleteSubtask(int id) {
        if (subTasks.containsKey(id)) {
            Subtask subtask = subTasks.get(id);
            Integer epicId = subtask.getEpicId();
            Epic epic = epicTasks.get(epicId);

            if (epic != null) {
                List<Task> epicSubtasks = epic.getSubtasks();
                epicSubtasks.remove(subtask);
                epic.setSubtasks(epicSubtasks);
            }

            subTasks.remove(id);
            historyManager.remove(id);
            return id;
        } else {
            throw new NotFoundException("Задачи с таким id не существует");
        }
    }

    @Override
    public int deleteEpic(int id) {
        if (epicTasks.containsKey(id)) {
            List<Subtask> epicSubtasks = getEpicSubtasks(id);

            for (Subtask subtask : epicSubtasks) {
                int subtaskId = subtask.getId();
                subTasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }

            epicTasks.remove(id);
            historyManager.remove(id);
            return id;
        } else {
            throw new NotFoundException("Задачи с таким id не существует");
        }
    }

    @Override
    public Integer getEpicIdByName(String name) {
        Optional<Integer> epicId = epicTasks.values()
                .stream()
                .filter(epic -> epic.getName().equals(name))
                .map(Task::getId)
                .findFirst();

        if (epicId.isEmpty()) {
            System.out.println("Эпик с таким названием не найден");
            return null;
        } else {
            return epicId.get();
        }
    }

    @Override
    public int createTask(Task task) {
        int taskId = task.getId();
        if (checkPeriodOverlap(task)) {
            throw new PeriodOverlapException("Вы пытаетесь добавить задачу на время, которое занято другой задачей.");
        } else {
            if (taskId > taskCounter) {
                taskCounter = taskId;
            }
            switch (task.getClass().getSimpleName()) {
                case "Epic" -> epicTasks.put(taskId, (Epic) task);
                case "Subtask" -> {
                    Subtask subtask = (Subtask) task;

                    if (subtask.getEpicId() != null) {
                        subTasks.put(taskId, subtask);
                        Epic epic = epicTasks.get(subtask.getEpicId());
                        addSubtaskToEpic(epic, subtask);
                    } else {
                        System.out.println("Вы пытаетесь добавить подзадачу без эпика");
                    }
                }
                case "Task" -> tasks.put(taskId, task);
                default -> System.out.println("Вы пытаетесь записать задачу неизвестного типа");
            }

            if (task.getStartTime() != null & !task.getClass().getSimpleName().equals("Epic")) {
                prioritizedTasks.add(task);
            }
        }
        return taskId;
    }

    @Override
    public int updateTask(int taskId, Task newTask) {
        if (checkPeriodOverlap(newTask)) {
            throw new PeriodOverlapException("Вы пытаетесь поставить задачу на время, которое занято другой задачей.");
        }
        switch (newTask.getClass().getSimpleName()) {
            case "Epic" -> {
                if (epicTasks.containsKey(taskId)) {
                    epicTasks.put(taskId, (Epic) newTask);
                } else {
                    throw new NotFoundException("Эпика с id=" + taskId + " не существует");
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
                    throw new NotFoundException("Подзадачи с id=" + taskId + " не существует");
                }
            }
            case "Task" -> {
                if (tasks.containsKey(taskId)) {
                    Task oldTask = tasks.get(taskId);
                    tasks.put(taskId, newTask);
                    prioritizedTasks.remove(oldTask);
                    prioritizedTasks.add(newTask);
                } else {
                    throw new NotFoundException("Задачи с id=" + taskId + " не существует");
                }
            }
            default -> System.out.println("Вы пытаетесь записать задачу неизвестного типа");
        }
        return taskId;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        if (epicTasks.containsKey(epicId)) {
            return epicTasks.get(epicId).getSubtasks()
                    .stream()
                    .map(task -> (Subtask) task)
                    .toList();
        } else {
            throw new NotFoundException("Эпик с id=" + epicId + " не найден.");
        }
    }

    public void addSubtaskToEpic(Epic epic, Subtask subtask) {
        List<Task> epicSubtasks = epic.getSubtasks();
        int epicId = epic.getId();
        epicSubtasks.remove(subtask);
        epicSubtasks.add(subtask);
        epic.setSubtasks(epicSubtasks);
        calculateEpicStatus(epicId);
    }

    public LocalDateTime getEpicStartTime(int epicId) {
        Optional<LocalDateTime> result = getEpicSubtasks(epicId)
                .stream()
                .map(Task::getStartTime)
                .min(Comparator.comparing(LocalDateTime::toString));
        return result.orElse(null);
    }

    public LocalDateTime getEpicEndTime(int epicId) {
        Optional<LocalDateTime> result = getEpicSubtasks(epicId)
                .stream()
                .map(Task::getStartTime)
                .max(Comparator.comparing(LocalDateTime::toString));
        return result.orElse(null);
    }

    public Duration getEpicDuration(int epicId) {
        return getEpicSubtasks(epicId)
                .stream()
                .map(Task::getDuration)
                .reduce(Duration.ofMinutes(0), Duration::plus);
    }

    public void calculateEpicStatus(int epicId) {
        Epic epic = epicTasks.get(epicId);
        epic.setDuration(getEpicDuration(epicId));
        epic.setStartTime(getEpicStartTime(epicId));
        epic.setEndTime(getEpicEndTime(epicId));
        List<Subtask> subtasks = getEpicSubtasks(epicId);

        Set<TaskStatus> taskStatuses =  subtasks.stream()
                .map(Task::getStatus)
                .collect(Collectors.toSet());

        if (taskStatuses.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (taskStatuses.size() == 1) {
            if (taskStatuses.contains(TaskStatus.NEW)) {
                epic.setStatus(TaskStatus.NEW);
            } else if (taskStatuses.contains(TaskStatus.DONE)) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean checkTwoTasksOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return true;
        }
        return !(task1.getStartTime().isAfter(task2.getEndTime()) || task1.getEndTime().isBefore(task2.getStartTime()));
    }

    public boolean checkPeriodOverlap(Task newTask) {
        long result = getPrioritizedTasks()
                .stream()
                .filter(task -> task.getId() != newTask.getId())
                .map(task -> checkTwoTasksOverlap(task, newTask))
                .filter(check -> check)
                .count();
        return result > 0;
    }
}
