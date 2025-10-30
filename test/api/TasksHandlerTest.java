package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;
import task.TaskType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class TasksHandlerTest {
    private final TaskManager taskManager = new InMemoryTaskManager();
    private final HttpTaskServer httpTaskServer;

    {
        try {
            httpTaskServer = new HttpTaskServer(taskManager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    public void beforeEach() throws IOException {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        httpTaskServer.start();

    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
    }

    @Test
    public void testTasksHandlerCreatesTask() throws IOException, InterruptedException {
        String taskJson = "{\"name\":\"Купить конфеты\",\"description\":\"Выбрать самые вкусные\",\"duration\": 30," +
                          "\"startTime\": \"2025-10-01T09:15\"}";
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .POST(HttpRequest.BodyPublishers.ofString(taskJson))
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(201, response.statusCode());

        // Проверяем список задач
        List<Task> tasks = taskManager.getTasks();
        Assertions.assertFalse(tasks.isEmpty(), "Задачи не возвращаются");
        Assertions.assertEquals(1, tasks.size(), "Некорректное количество задач");
        Assertions.assertEquals("Купить конфеты", tasks.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testTasksHandlerUpdatesTask() throws IOException, InterruptedException {
        taskManager.createTask(
                new Task(
                        "Сделать зарядку",
                        "Много разных упражнений",
                        1,
                        TaskStatus.NEW,
                        TaskType.TASK,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00")
                )
        );
        String taskJson = "{\"name\":\"Купить конфеты\",\"description\":\"Выбрать самые вкусные\",\"duration\":30," +
                "\"startTime\":\"2025-10-01T09:15\",\"id\":1}";
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(201, response.statusCode());

        // Проверяем список задач
        List<Task> tasks = taskManager.getTasks();
        Assertions.assertFalse(tasks.isEmpty(), "Задачи не возвращаются");
        Assertions.assertEquals(1, tasks.size(), "Некорректное количество задач");
        Assertions.assertEquals("Купить конфеты", tasks.getFirst().getName(), "Название задачи не изменилось");
    }

    @Test
    public void testTasksHandlerDeletesTask() throws IOException, InterruptedException {
        taskManager.createTask(
                new Task(
                        "Сделать зарядку",
                        "Много разных упражнений",
                        1,
                        TaskStatus.NEW,
                        TaskType.TASK,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00")
                )
        );
        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        // Проверяем список задач
        List<Task> tasks = taskManager.getTasks();
        Assertions.assertTrue(tasks.isEmpty(), "Задача не удалилась");
    }

    @Test
    public void testTasksHandlerGetsTaskById() throws IOException, InterruptedException {
        taskManager.createTask(
                new Task(
                        "Сделать зарядку",
                        "Много разных упражнений",
                        1,
                        TaskStatus.NEW,
                        TaskType.TASK,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00")
                )
        );
        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        // Проверяем тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());
        Assertions.assertEquals(1, jsonElement.getAsJsonObject().get("id").getAsInt(), "Отличается id");
        Assertions.assertEquals("Сделать зарядку", jsonElement.getAsJsonObject().get("name").getAsString(), "Отличается название");
    }

    @Test
    public void testTasksHandlerGetsTasks() throws IOException, InterruptedException {
        taskManager.createTask(
                new Task(
                        "Сделать зарядку",
                        "Много разных упражнений",
                        1,
                        TaskStatus.NEW,
                        TaskType.TASK,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00")
                )
        );
        taskManager.createTask(
                new Task(
                        "Купить конфеты",
                        "Выбрать самые вкусные",
                        2,
                        TaskStatus.NEW,
                        TaskType.TASK,
                        30,
                        LocalDateTime.parse("2025-12-02T12:00:00")
                )
        );
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        // Проверяем тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());
        Assertions.assertTrue(jsonElement.isJsonArray(), "Возвращён не список");
        Assertions.assertEquals(2, jsonElement.getAsJsonArray().size(), "Возвращено неверное число задач");
        Assertions.assertEquals(
                "Купить конфеты",
                jsonElement.getAsJsonArray().get(1).getAsJsonObject().get("name").getAsString(),
                "Возвращено неверное название второй задачи"
        );
    }

    @Test
    public void testTasksHandlerDealsWithErrors() throws IOException, InterruptedException {
        taskManager.createTask(
                new Task(
                        "Сделать зарядку",
                        "Много разных упражнений",
                        1,
                        TaskStatus.NEW,
                        TaskType.TASK,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00")
                )
        );
        taskManager.createTask(
                new Task(
                        "Купить конфеты",
                        "Выбрать самые вкусные",
                        2,
                        TaskStatus.NEW,
                        TaskType.TASK,
                        30,
                        LocalDateTime.parse("2025-12-02T12:00:00")
                )
        );
        // Запрашиваем несуществующую задачу
        URI uri = URI.create("http://localhost:8080/tasks/10");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        // Пытаемся удалить несуществующую задачу
        uri = URI.create("http://localhost:8080/tasks/10");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        // Пытаемся обновить несуществующую задачу
        String taskJson = "{\"name\":\"Купить конфеты\",\"description\":\"Выбрать самые вкусные\",\"duration\":30," +
                "\"startTime\":\"2025-10-01T09:15\",\"id\":10}";
        uri = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        // Пытаемся сделать задачу пересекающейся
        taskJson = "{\"name\":\"Купить конфеты\",\"description\":\"Выбрать самые вкусные\",\"duration\":30," +
                "\"startTime\":\"2025-12-01T12:00\",\"id\":2}";
        uri = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());

        // Пытаемся создать задачу, пересекающуюся с другой задачей
        taskJson = "{\"name\":\"Купить конфеты\",\"description\":\"Выбрать самые вкусные\",\"duration\":30," +
                "\"startTime\":\"2025-12-01T12:00\"}";
        uri = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());

        // Пробуем обратиться по несуществующему пути
        uri = URI.create("http://localhost:8080/tasks/10/some/strange/path");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }
}




