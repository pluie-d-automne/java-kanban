package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import manager.PeriodOverlapException;
import manager.TaskManager;
import task.Task;
import task.TaskStatus;
import task.TaskType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    Gson gson = new GsonBuilder().create();

    TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] path = httpExchange.getRequestURI().getPath().split("/");

        if (method.equals("GET") & path.length == 2) {
            System.out.println("GET /tasks");
            List<TaskView> tasks = taskManager.getTasks().stream().map(this::taskToPojo).toList();
            sendText(httpExchange, gson.toJson(tasks));
        } else if (method.equals("GET") & path.length == 3) {
            System.out.println("GET /tasks/{id}");
            int taskId = Integer.parseInt(path[2]);
            Task task = taskManager.getTaskById(taskId);
            sendText(httpExchange, gson.toJson(taskToPojo(task)));
        } else if (method.equals("POST") & path.length == 2) {
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            TaskView taskView = gson.fromJson(body, TaskView.class);
            if (taskView.id == 0) {
                System.out.println("Создаём новую задачу");
                try {
                    int taskId = taskManager.createTask(
                            new Task(
                                    taskView.name,
                                    taskView.description,
                                    taskManager.createTaskId(),
                                    TaskStatus.NEW,
                                    TaskType.TASK,
                                    taskView.duration,
                                    LocalDateTime.parse(taskView.startTime)
                            )
                    );
                    sendSuccess(httpExchange, "Создана задача с id=" + taskId);
                } catch (PeriodOverlapException e) {
                    sendHasOverlaps(httpExchange, e.getMessage());
                }
            } else {
                System.out.println("Надо обновить задачу " + taskView.id);
            }
        } else if (method.equals("DELETE") & path.length == 3) {
            int taskId = Integer.parseInt(path[2]);
            taskManager.dropTaskById(taskId);
        }
    }

    public TaskView taskToPojo(Task task) {

        return new TaskView(
                task.getName(),
                task.getDescription(),
                task.getId(),
                task.getStatus(),
                task.getTaskType(),
                task.getDuration().toMinutes(),
                task.getStartTime().toString()
        );

    }

    public static class TaskView {
        private String name;
        private String description;
        private int id;
        private TaskStatus status;
        private TaskType taskType;
        private long duration;
        private String startTime;

        public TaskView (String name,
                         String desc,
                         int id,
                         TaskStatus status,
                         TaskType taskType,
                         long duration,
                         String startTime
        ) {
            this.name = name;
            this.description = desc;
            this.id = id;
            this.status = status;
            this.taskType = taskType;
            this.duration = duration;
            this.startTime = startTime;
        }

        public TaskView (String name,
                         String desc,
                         long duration,
                         String startTime
        ) {
            this.name = name;
            this.description = desc;
            this.duration = duration;
            this.startTime = startTime;
        }
    }

    class TaskViewListTypeToken extends TypeToken<List<TaskView>> {
        // здесь ничего не нужно реализовывать
    }
}

