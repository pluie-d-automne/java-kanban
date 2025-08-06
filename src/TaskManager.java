import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> standaloneTasks;
    HashMap<Integer, Task> epicTasks;
    HashMap<Integer, Task> subTasks;
    public int taskCounter;

    public TaskManager() {
        this.standaloneTasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.taskCounter = 0;
    }

    public int createTaskId() {
        taskCounter += 1;
        return taskCounter;
    }

    public HashMap<Integer, Task> getStandaloneTasks() {
        return standaloneTasks;
    }

    public HashMap<Integer, Task> getEpicTasks() {
        return epicTasks;
    }

    public HashMap<Integer, Task> getSubTasks() {
        return subTasks;
    }

    public void deleteAllStandaloneTasks() {
        standaloneTasks = new HashMap<>();
        System.out.println("Все задачи удалены");
    }

    public void deleteAllEpics() {
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
        System.out.println("Все эпики с подзадачами удалены");
    }

    public void deleteAllSubtasks() {
        subTasks = new HashMap<>();
        System.out.println("Все подзадачи удалены");
    }

    public Task getTaskById(int id) {
        if (standaloneTasks.containsKey(id)) {
            return standaloneTasks.get(id);
        } else if (epicTasks.containsKey(id)) {
            return epicTasks.get(id);
        } else if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        } else {
            System.out.println("Задачи с таким id не существует");
            return null;
        }
    }

    public void dropTaskById(int id) {
        if (standaloneTasks.containsKey(id)) {
            standaloneTasks.remove(id);
        } else if (epicTasks.containsKey(id)) {
            HashMap<Integer, Task> epicSubtasks = getEpicSubtasks(id);
            for (Task task : epicSubtasks.values()) {
                Subtask subtask = (Subtask) task;
                subtask.dropEpicId();
                epicSubtasks.put(subtask.getId(), subtask);
            }
            epicTasks.remove(id);
        } else if (subTasks.containsKey(id)) {
            Subtask subtask = (Subtask) subTasks.get(id);
            Integer epicId = subtask.getEpicId();
            Epic epic = (Epic) epicTasks.get(epicId);
            if (epic!=null) {epic.removeSubtask(id);}
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

    public void createTask(Task task) {
        switch (task.getClass().getSimpleName()) {
            case "Epic" -> epicTasks.put(task.getId(), task);
            case "Subtask" -> {
                int taskId = task.getId();
                subTasks.put(task.getId(), task);
                Subtask subtask = (Subtask) subTasks.get(taskId);
                if (subtask.getEpicId()!=null) {
                    Epic epic = (Epic) epicTasks.get(subtask.getEpicId());
                    epic.addSubtask(taskId, task);
                }
            }
            default -> standaloneTasks.put(task.getId(), task);
        }
    }

    public void updateTask(int taskId, Task newTask) {
        dropTaskById(taskId);
        String newTaskType = newTask.getClass().getSimpleName();
        if (newTaskType.equals("Epic")) {
            epicTasks.put(taskId, newTask);
        } else if (newTaskType.equals("Subtask")) {
            subTasks.put(taskId, newTask);
            Subtask subtask = (Subtask) newTask;
            if (subtask.getEpicId()!=null) {
                Epic epic = (Epic) epicTasks.get(subtask.getEpicId());
                epic.addSubtask(taskId, newTask);
            }
        } else {
            standaloneTasks.put(taskId, newTask);
        }
    }

    public HashMap<Integer, Task> getEpicSubtasks(int epicId){
        Epic epic = (Epic) epicTasks.get(epicId);
        return epic.getSubtasks();
    }

}
