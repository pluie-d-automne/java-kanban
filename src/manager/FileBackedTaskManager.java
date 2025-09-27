package manager;

import task.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final String filePath;
    public static final String CSV_HEAD = "id,type,name,status,description,epicId\n";

    public FileBackedTaskManager(String filePath) {
        this.filePath = filePath;
        save();
    }

    static FileBackedTaskManager loadFromFile(File file) {
        String fileData;
        try {
            fileData = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] allTasks = fileData.split("\n");
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toPath().toString());
        for (int i = 1; i < allTasks.length; i++) {
            manager.createTask(manager.fromString(allTasks[i]));
        }
        return manager;
    }

    private void save() {
        try {
            Writer fileWriter = new FileWriter(filePath, false);
            fileWriter.write(CSV_HEAD);

            for (int taskId: tasks.keySet()) {
                Task task = tasks.get(taskId);
                fileWriter.write(task.toString()+"\n");
            }

            for (int taskId: epicTasks.keySet()) {
                Epic task = epicTasks.get(taskId);
                fileWriter.write(task.toString()+"\n");
            }

            for (int taskId: subTasks.keySet()) {
                Subtask task = subTasks.get(taskId);
                fileWriter.write(task.toString()+"\n");
            }

            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Проблемы с записью в файл");
        }


    }

    private Task fromString(String value) {
        String[] taskData = value.split(",");
        int id = Integer.parseInt(taskData[0]);
        String name = taskData[2];

        TaskStatus status = switch (taskData[3]) {
            case "DONE" -> TaskStatus.DONE;
            case "NEW" -> TaskStatus.NEW;
            default -> TaskStatus.IN_PROGRESS;
        };

        String description = taskData[4];
        int epicId = Integer.parseInt(taskData[5]);

        switch (taskData[1]) {
            case "EPIC" -> {return new Epic(name, description, id, status, TaskType.EPIC);}
            case "SUBTASK" -> {return new Subtask(name, description, id, status, TaskType.SUBTASK, epicId);}
            default -> {return new Task(name, description, id, status, TaskType.TASK);}
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public int dropTaskById(int id) {
        int taskId = super.dropTaskById(id);
        save();
        return taskId;
    }

    @Override
    public int createTask(Task task) {
        int taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public int updateTask(int taskId, Task newTask) {
        int id = super.updateTask(taskId, newTask);
        save();
        return id;
    }

    @Override
    public void addSubtaskToEpic(Epic epic, Subtask subtask) {
        super.addSubtaskToEpic(epic, subtask);
        save();
    }

    @Override
    public void calculateEpicStatus(int epicId) {
        super.calculateEpicStatus(epicId);
        save();
    }
}
