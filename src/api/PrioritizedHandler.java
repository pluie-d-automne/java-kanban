package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder().create();

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] path = httpExchange.getRequestURI().getPath().split("/");

        if (method.equals("GET") & path.length == 2) {
            List<Task> tasks = taskManager.getPrioritizedTasks();
            List<TasksHandler.TaskView> prioritized = tasks.stream().map(TasksHandler::taskToPojo).toList();
            sendText(httpExchange, gson.toJson(prioritized));
        } else {
            sendNotFound(httpExchange, "Указанный метод + путь не найден");
        }
    }
}
