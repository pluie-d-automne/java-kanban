package api;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    public static TaskManager taskManager;
    static HttpServer httpServer;

    HttpTaskServer (TaskManager taskManager) {
        HttpTaskServer.taskManager = taskManager;
    }

    public static void main(String[] args) throws IOException {
        if (taskManager == null) {
            System.out.println("Создаём таск-менеджер по умолчанию");
            File file = File.createTempFile("kanban", "csv");
            taskManager = Managers.getDefault(file.getPath());
        }
        start();
    }

    public static void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritised", new PrioritizedHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public static void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }
}
