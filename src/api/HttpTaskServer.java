package api;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import task.Task;
import task.TaskStatus;
import task.TaskType;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        File file = File.createTempFile("kanban", "csv");
        TaskManager taskManager = Managers.getDefault(file.getPath());
        taskManager.createTask(
                new Task(
                        "Сходить в магазн",
                        "Купить хлеб молоко шоколадку",
                        taskManager.createTaskId(),
                        TaskStatus.NEW,
                        TaskType.TASK,
                        30,
                        LocalDateTime.parse("2025-10-01T07:15:00")
                )
        );

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler());
        httpServer.createContext("/epics", new EpicsHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritixed", new PrioritizedHandler());

        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

    }
}
