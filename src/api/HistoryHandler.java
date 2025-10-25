package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    Gson gson = new GsonBuilder().create();

    HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] path = httpExchange.getRequestURI().getPath().split("/");

        if (method.equals("GET") & path.length == 2) {
            List<Task> tasks = taskManager.getHistory();
            List<TasksHandler.TaskView> history = tasks.stream().map(TasksHandler::taskToPojo).toList();
            sendText(httpExchange, gson.toJson(history));
        } else {
            sendNotFound(httpExchange, "Указанный метод + путь не найден");
        }
    }
}
