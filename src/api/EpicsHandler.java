package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.ManagerSaveException;
import manager.NotFoundException;
import manager.PeriodOverlapException;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.TaskStatus;
import task.TaskType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    Gson gson = new GsonBuilder().create();

    EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] path = httpExchange.getRequestURI().getPath().split("/");

        if (method.equals("GET") & path.length == 2) {
            System.out.println("Выводим список эпиков.");
            List<EpicView> tasks = taskManager.getEpicTasks().stream().map(this::epicToPojo).toList();
            sendText(httpExchange, gson.toJson(tasks));
        } else if (method.equals("GET") & path.length == 3) {
            System.out.println("Выводим эпик по id");
            int taskId = Integer.parseInt(path[2]);
            try{
                Epic task = taskManager.getEpicById(taskId);
                sendText(httpExchange, gson.toJson(epicToPojo(task)));
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else if (method.equals("GET") & path.length == 4 & path[3].equals("subtasks")) {
            System.out.println("Выводим подзадачи для эпика");
            int taskId = Integer.parseInt(path[2]);
            try {
                List<Subtask> subtasks = taskManager.getEpicSubtasks(taskId);
                List<SubtasksHandler.SubtaskView> tasks = subtasks.stream().map(SubtasksHandler::subtaskToPojo).toList();
                sendText(httpExchange, gson.toJson(tasks));
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else if (method.equals("POST") & path.length == 2) {
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            EpicView epicView = gson.fromJson(body, EpicView.class);
            if (epicView.id == 0) {
                System.out.println("Создаём новый эпик.");
                try {
                    int taskId = taskManager.createTask(
                            new Epic(
                                    epicView.name,
                                    epicView.description,
                                    taskManager.createTaskId(),
                                    TaskStatus.NEW,
                                    TaskType.EPIC,
                                    epicView.duration,
                                    LocalDateTime.parse(epicView.startTime)
                            )
                    );
                    sendSuccess(httpExchange, "Создан эпик с id=" + taskId + ".");
                } catch (PeriodOverlapException e) {
                    sendHasOverlaps(httpExchange, e.getMessage());
                } catch (ManagerSaveException e) {
                    sendInternalServerError(httpExchange);
                }
            } else {
                System.out.println("Обновляем эпик с id=" + epicView.id + ".");
                try {
                    int taskId = taskManager.updateTask(
                            epicView.id,
                            new Epic(
                                    epicView.name,
                                    epicView.description,
                                    epicView.id,
                                    epicView.status==null ? TaskStatus.NEW : epicView.status,
                                    TaskType.EPIC,
                                    epicView.duration,
                                    LocalDateTime.parse(epicView.startTime)
                            )
                    );
                    sendSuccess(httpExchange, "Эпик с id=" + taskId + " успешно обновлён.");
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange, e.getMessage());
                } catch (ManagerSaveException e) {
                    sendInternalServerError(httpExchange);
                }
            }
        } else if (method.equals("DELETE") & path.length == 3) {
            int taskId = Integer.parseInt(path[2]);
            try {
                taskManager.deleteEpic(taskId);
                sendSuccess(httpExchange, "Задача с id=" + taskId +" успешно удалена.");
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            } catch (ManagerSaveException e) {
                sendInternalServerError(httpExchange);
            }
        } else {
            sendNotFound(httpExchange, "Указанный метод + путь не найден");
        }
    }

    public EpicView epicToPojo(Epic epic) {

        return new EpicView(
                epic.getName(),
                epic.getDescription(),
                epic.getId(),
                epic.getStatus(),
                epic.getTaskType(),
                epic.getDuration().toMinutes(),
                epic.getStartTime().toString()
        );

    }

    public static class EpicView {
        private final String name;
        private final String description;
        private int id;
        private TaskStatus status;
        private TaskType taskType;
        private final long duration;
        private final String startTime;

        public EpicView (
                String name,
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

        public EpicView (
                String name,
                String desc,
                long duration,
                String startTime
        ) {
            this.name = name;
            this.description = desc;
            this.duration = duration;
            this.startTime = startTime;
        }

        public EpicView (
                String name,
                String desc,
                int id,
                TaskStatus status,
                long duration,
                String startTime
        ) {
            this.name = name;
            this.description = desc;
            this.id = id;
            this.status = status;
            this.duration = duration;
            this.startTime = startTime;
        }
    }
}
