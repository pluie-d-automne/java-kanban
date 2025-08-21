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
            return tasks.get(id);
        } else {
            System.out.println("Задачи с таким id не существует");
            return null;
        }
    }

    @Override
    public Task getEpicById(int id) {
        if (epicTasks.containsKey(id)) {
            return epicTasks.get(id);
        } else {
            System.out.println("Эпика с таким id не существует");
            return null;
        }
    }

    @Override
    public Task getSubtaskById(int id) {
        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
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
            List<Subtask> epicSubtasks = getEpicSubtasks(id);
            for (Subtask subtask : epicSubtasks) {
                subtask.setEpicId(null);
            }
            epicTasks.remove(id);
        } else if (subTasks.containsKey(id)) {
            Subtask subtask = subTasks.get(id);
            Integer epicId = subtask.getEpicId();
            Epic epic = epicTasks.get(epicId);
            if (epic!=null) {
                List<Integer> epicSubtasks = epic.getSubtasks();
                epicSubtasks.remove((Integer) id);
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
        switch (task.getClass().getSimpleName()) {
            case "Epic" -> epicTasks.put(taskId, (Epic) task);
            case "Subtask" -> {
                Subtask subtask = (Subtask) task;
                subTasks.put(taskId, subtask);
                if (subtask.getEpicId() != null) {
                    Epic epic = epicTasks.get(subtask.getEpicId());
                    addSubtaskToEpic(epic, taskId);
                }
            }
            default -> tasks.put(taskId, task);
        }
        return taskId;
    }

    @Override
    public int updateTask(int taskId, Task newTask) {
        String newTaskType = newTask.getClass().getSimpleName();
        if (newTaskType.equals("Epic")) {
            if (epicTasks.containsKey(taskId)) {
                epicTasks.put(taskId, (Epic) newTask);
            } else {
                System.out.println("Эпика с таким taskId не существует");
            }
        } else if (newTaskType.equals("Subtask")) {
            if (subTasks.containsKey(taskId)) {
                Subtask subtask = (Subtask) newTask;
                subTasks.put(taskId, subtask);
                Integer epicId = subtask.getEpicId();
                if (epicId != null) {
                    Epic epic = epicTasks.get(epicId);
                    addSubtaskToEpic(epic, taskId);
                    calculateEpicStatus(epicId);
                }
            } else {
                System.out.println("Подзадачи с таким taskId не существует");
            }
        } else {
            if (tasks.containsKey(taskId)) {
                tasks.put(taskId, newTask);
            } else {
                System.out.println("Задачи с таким taskId не существует");
            }
        }
        return taskId;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId){
        Epic epic = (Epic) epicTasks.get(epicId);
        List<Integer> subtaskIds = epic.getSubtasks();
        List<Subtask> subtasks = new ArrayList<>();
        for (int id : subtaskIds) {
            subtasks.add(subTasks.get(id));
        }
        return subtasks;
    }

    public void addSubtaskToEpic(Epic epic, Integer subtaskId) {
        List<Integer> epicSubtasks = epic.getSubtasks();
        epicSubtasks.add(subtaskId);
        epic.setSubtasks(epicSubtasks);
    }

    public void calculateEpicStatus(int epicId) {
        List<TaskStatus> taskStatuses = new ArrayList<>();
        List<Subtask> subtasks = getEpicSubtasks(epicId);
        Epic epic = epicTasks.get(epicId);
        TaskStatus status;
        for (Subtask subtask : subtasks) {
            status = subtask.getStatus();
            if (! taskStatuses.contains(status)) {
                taskStatuses.add(status);
            }
        }
        if (taskStatuses.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (taskStatuses.size()==1) {
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
}
