package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class SubtaskHandlerTest {
    private final TaskManager taskManager = new InMemoryTaskManager();
    private HttpTaskServer httpTaskServer;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    public void beforeEach() throws IOException {
        httpTaskServer = new HttpTaskServer(taskManager);
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
    public void tesSubtasksHandlerCreatesSubtask() throws IOException, InterruptedException {
        taskManager.createTask(
                new Epic(
                        "Подготовка к Новому году",
                        "Много разных важных дел",
                        1,
                        TaskStatus.NEW,
                        TaskType.EPIC,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00")
                )
        );
        String subtaskJson = "{\"name\":\"Купить конфеты\",\"description\":\"Выбрать самые вкусные\",\"duration\": 30," +
                          "\"startTime\": \"2025-12-01T12:00:00\",\"epicId\":1}";
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(201, response.statusCode());

        // Проверяем список задач
        List<Subtask> subtasks = taskManager.getSubTasks();
        Assertions.assertFalse(subtasks.isEmpty(), "Подзадачи не возвращаются");
        Assertions.assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        Assertions.assertEquals("Купить конфеты", subtasks.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testSubtasksHandlerUpdatesSubtask() throws IOException, InterruptedException {
        taskManager.createTask(
                new Epic(
                        "Подготовка к Новому году",
                        "Много разных важных дел",
                        1,
                        TaskStatus.NEW,
                        TaskType.EPIC,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00")
                )
        );
        taskManager.createTask(
                new Subtask(
                        "Сделать зарядку",
                        "Много разных упражнений",
                        2,
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00"),
                        1
                )
        );
        String subtaskJson = "{\"name\":\"Купить конфеты\",\"description\":\"Выбрать самые вкусные\",\"duration\":30," +
                "\"startTime\":\"2025-10-01T09:15\",\"epicId\":1,\"id\":2}";
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(201, response.statusCode());

        // Проверяем список задач
        List<Subtask> subtasks = taskManager.getSubTasks();
        Assertions.assertFalse(subtasks.isEmpty(), "Подзадачи не возвращаются");
        Assertions.assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        Assertions.assertEquals("Купить конфеты", subtasks.getFirst().getName(), "Название подзадачи не изменилось");
    }

    @Test
    public void testSubtasksHandlerDeletesSubtask() throws IOException, InterruptedException {
        taskManager.createTask(
                new Epic(
                        "Подготовка к Новому году",
                        "Много разных важных дел",
                        1,
                        TaskStatus.NEW,
                        TaskType.EPIC,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00")
                )
        );
        int subtask_id = taskManager.createTask(
                new Subtask(
                        "Сделать зарядку",
                        "Много разных упражнений",
                        2,
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00"),
                        1
                )
        );
        URI uri = URI.create("http://localhost:8080/subtasks/" + subtask_id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        // Проверяем список задач
        List<Subtask> subtasks = taskManager.getSubTasks();
        Assertions.assertTrue(subtasks.isEmpty(), "Подзадача не удалилась");
    }

    @Test
    public void testSubtasksHandlerGetsSubtaskById() throws IOException, InterruptedException {
        taskManager.createTask(
                new Epic(
                        "Подготовка к Новому году",
                        "Много разных важных дел",
                        1,
                        TaskStatus.NEW,
                        TaskType.EPIC,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00")
                )
        );
        int subtask_id = taskManager.createTask(
                new Subtask(
                        "Сделать зарядку",
                        "Много разных упражнений",
                        2,
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00"),
                        1
                )
        );
        URI uri = URI.create("http://localhost:8080/subtasks/" + subtask_id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        // Проверяем тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());
        Assertions.assertEquals(subtask_id, jsonElement.getAsJsonObject().get("id").getAsInt(), "Отличается id");
        Assertions.assertEquals("Сделать зарядку", jsonElement.getAsJsonObject().get("name").getAsString(), "Отличается название");
    }

    @Test
    public void testSubtasksHandlerGetsSubtasks() throws IOException, InterruptedException {
        taskManager.createTask(
                new Epic(
                        "Подготовка к Новому году",
                        "Много разных важных дел",
                        1,
                        TaskStatus.NEW,
                        TaskType.EPIC,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00")
                )
        );
        taskManager.createTask(
                new Subtask(
                        "Сделать зарядку",
                        "Много разных упражнений",
                        2,
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00"),
                        1
                )
        );
        taskManager.createTask(
                new Subtask(
                        "Купить конфеты",
                        "Выбрать самые вкусные",
                        3,
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        30,
                        LocalDateTime.parse("2025-12-02T12:00:00"),
                        1
                )
        );
        URI uri = URI.create("http://localhost:8080/subtasks");
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
                "Возвращено неверное название второй подзадачи"
        );
    }

    @Test
    public void tesSubtasksHandlerDealsWithErrors() throws IOException, InterruptedException {
        taskManager.createTask(
                new Epic(
                        "Подготовка к Новому году",
                        "Много разных важных дел",
                        1,
                        TaskStatus.NEW,
                        TaskType.EPIC,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00")
                )
        );
        taskManager.createTask(
                new Subtask(
                        "Сделать зарядку",
                        "Много разных упражнений",
                        2,
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00"),
                        1
                )
        );
        taskManager.createTask(
                new Subtask(
                        "Купить конфеты",
                        "Выбрать самые вкусные",
                        3,
                        TaskStatus.NEW,
                        TaskType.SUBTASK,
                        30,
                        LocalDateTime.parse("2025-12-02T12:00:00"),
                        1
                )
        );
        // Запрашиваем несуществующую подзадачу
        URI uri = URI.create("http://localhost:8080/subtasks/10");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        // Пытаемся удалить несуществующую подзадачу
        uri = URI.create("http://localhost:8080/subtasks/10");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        // Пытаемся обновить несуществующую подзадачу
        String subtaskJson = "{\"name\":\"Купить конфеты\",\"description\":\"Выбрать самые вкусные\",\"duration\":30," +
                "\"startTime\":\"2025-10-01T09:15\",\"epicId\":1,\"id\":10}";
        uri = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        // Пытаемся сделать подзадачу пересекающейся
        subtaskJson = "{\"name\":\"Купить конфеты\",\"description\":\"Выбрать самые вкусные\",\"duration\":30," +
                "\"startTime\":\"2025-12-01T12:00\",\"epicId\":1,\"id\":3}";
        uri = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());

        // Пытаемся создать подзадачу, пересекающуюся с другой задачей
        subtaskJson = "{\"name\":\"Купить конфеты снова\",\"description\":\"Выбрать самые вкусные\",\"duration\":30," +
                "\"startTime\":\"2025-12-01T12:00\",\"epicId\":1}";
        uri = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());

        // Пробуем обратиться по несуществующему пути
        uri = URI.create("http://localhost:8080/subtasks/10/some/strange/path");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }
}




