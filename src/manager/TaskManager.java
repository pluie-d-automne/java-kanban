package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private int taskCounter = 0;


    public int createTaskId() {
        taskCounter += 1;
        return taskCounter;
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<Subtask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены");
    }

    public void deleteAllEpics() {
        epicTasks.clear();
        subTasks.clear();
        System.out.println("Все эпики с подзадачами удалены");
    }

    public void deleteAllSubtasks() {
        subTasks.clear();
        System.out.println("Все подзадачи удалены");
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else {
            System.out.println("Задачи с таким id не существует");
            return null;
        }
    }

    public Task getEpicById(int id) {
        if (epicTasks.containsKey(id)) {
            return epicTasks.get(id);
        } else {
            System.out.println("Эпика с таким id не существует");
            return null;
        }
    }

    public Task getSubtaskById(int id) {
        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        } else {
            System.out.println("Подзадачи с таким id не существует");
            return null;
        }
    }

    public void dropTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epicTasks.containsKey(id)) {
            ArrayList<Subtask> epicSubtasks = getEpicSubtasks(id);
            for (Subtask subtask : epicSubtasks) {
                subtask.setEpicId(null);
            }
            epicTasks.remove(id);
        } else if (subTasks.containsKey(id)) {
            Subtask subtask = subTasks.get(id);
            Integer epicId = subtask.getEpicId();
            Epic epic = epicTasks.get(epicId);
            if (epic!=null) {
                ArrayList<Integer> epicSubtasks = epic.getSubtasks();
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

    public int updateTask(int taskId, Task newTask) {
        dropTaskById(taskId);
        String newTaskType = newTask.getClass().getSimpleName();
        if (newTaskType.equals("Epic")) {
            epicTasks.put(taskId, (Epic) newTask);
        } else if (newTaskType.equals("Subtask")) {
            Subtask subtask = (Subtask) newTask;
            subTasks.put(taskId, subtask);
            Integer epicId = subtask.getEpicId();
            if (epicId != null) {
                Epic epic = epicTasks.get(epicId);
                addSubtaskToEpic(epic, taskId);
                calculateEpicStatus(epicId);
            }
        } else {
            tasks.put(taskId, newTask);
        }
        return taskId;
    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId){
        Epic epic = (Epic) epicTasks.get(epicId);
        ArrayList<Integer> subtaskIds = epic.getSubtasks();
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int id : subtaskIds) {
            subtasks.add(subTasks.get(id));
        }
        return subtasks;
    }

    public void addSubtaskToEpic(Epic epic, Integer subtaskId) {
        ArrayList<Integer> epicSubtasks = epic.getSubtasks();
        epicSubtasks.add(subtaskId);
        epic.setSubtasks(epicSubtasks);
    }

    public void calculateEpicStatus(int epicId) {
        ArrayList<TaskStatus> taskStatuses = new ArrayList<>();
        ArrayList<Subtask> subtasks = getEpicSubtasks(epicId);
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
