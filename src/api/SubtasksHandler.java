package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.ManagerSaveException;
import manager.NotFoundException;
import manager.PeriodOverlapException;
import manager.TaskManager;
import task.Subtask;
import task.TaskStatus;
import task.TaskType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    Gson gson = new GsonBuilder().create();

    SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] path = httpExchange.getRequestURI().getPath().split("/");

        if (method.equals("GET") & path.length == 2) {
            System.out.println("Выводим список подзадач.");
            List<SubtaskView> subtasks = taskManager.getSubTasks().stream().map(SubtasksHandler::subtaskToPojo).toList();
            sendText(httpExchange, gson.toJson(subtasks));
        } else if (method.equals("GET") & path.length == 3) {
            System.out.println("Выводим подзадачу по id");
            int taskId = Integer.parseInt(path[2]);
            try{
                Subtask subtask = taskManager.getSubtaskById(taskId);
                sendText(httpExchange, gson.toJson(subtaskToPojo(subtask)));
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else if (method.equals("POST") & path.length == 2) {
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            SubtaskView subtaskView = gson.fromJson(body, SubtaskView.class);
            if (subtaskView.id == 0) {
                System.out.println("Создаём новую подзадачу.");
                try {
                    int taskId = taskManager.createTask(
                            new Subtask(
                                    subtaskView.name,
                                    subtaskView.description,
                                    taskManager.createTaskId(),
                                    TaskStatus.NEW,
                                    TaskType.SUBTASK,
                                    subtaskView.duration,
                                    LocalDateTime.parse(subtaskView.startTime),
                                    subtaskView.epicId
                            )
                    );
                    sendSuccess(httpExchange, "Создана подзадача с id=" + taskId + ".");
                } catch (PeriodOverlapException e) {
                    sendHasOverlaps(httpExchange, e.getMessage());
                } catch (ManagerSaveException e) {
                    sendInternalServerError(httpExchange);
                }
            } else {
                System.out.println("Обновляем подзадачу задачу с id=" + subtaskView.id + ".");
                try {
                    int taskId = taskManager.updateTask(
                            subtaskView.id,
                            new Subtask(
                                    subtaskView.name,
                                    subtaskView.description,
                                    subtaskView.id,
                                    subtaskView.status==null ? TaskStatus.NEW : subtaskView.status,
                                    TaskType.SUBTASK,
                                    subtaskView.duration,
                                    LocalDateTime.parse(subtaskView.startTime),
                                    subtaskView.epicId
                            )
                    );
                    sendSuccess(httpExchange, "Подзадача с id=" + taskId + " успешно обновлена.");
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange, e.getMessage());
                } catch (ManagerSaveException e) {
                    sendInternalServerError(httpExchange);
                } catch (PeriodOverlapException e) {
                    sendHasOverlaps(httpExchange, e.getMessage());
                }
            }
        } else if (method.equals("DELETE") & path.length == 3) {
            int taskId = Integer.parseInt(path[2]);
            try {
                taskManager.deleteSubtask(taskId);
                sendText(httpExchange, "{\"action\":\"subtask_deleted\",\"task_id\":" + taskId +"}");
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            } catch (ManagerSaveException e) {
                sendInternalServerError(httpExchange);
            }
        } else {
            sendNotFound(httpExchange, "Указанный метод + путь не найден");
        }
    }

    public static SubtaskView subtaskToPojo(Subtask subtask) {

        return new SubtaskView(
                subtask.getName(),
                subtask.getDescription(),
                subtask.getId(),
                subtask.getEpicId(),
                subtask.getStatus(),
                subtask.getTaskType(),
                subtask.getDuration().toMinutes(),
                subtask.getStartTime().toString()
        );

    }

    public static class SubtaskView {
        private final String name;
        private final String description;
        private int id;
        private final int epicId;
        private TaskStatus status;
        private TaskType taskType;
        private final long duration;
        private final String startTime;

        public SubtaskView (
                String name,
                String desc,
                int id,
                int epicId,
                TaskStatus status,
                TaskType taskType,
                long duration,
                String startTime
        ) {
            this.name = name;
            this.description = desc;
            this.id = id;
            this.epicId = epicId;
            this.status = status;
            this.taskType = taskType;
            this.duration = duration;
            this.startTime = startTime;
        }

        public SubtaskView (
                String name,
                String desc,
                long duration,
                String startTime,
                int epicId
        ) {
            this.name = name;
            this.description = desc;
            this.duration = duration;
            this.startTime = startTime;
            this.epicId = epicId;
        }

        public SubtaskView (
                String name,
                String desc,
                int id,
                TaskStatus status,
                long duration,
                String startTime,
                int epicId
        ) {
            this.name = name;
            this.description = desc;
            this.id = id;
            this.epicId = epicId;
            this.status = status;
            this.duration = duration;
            this.startTime = startTime;
        }
    }
}
