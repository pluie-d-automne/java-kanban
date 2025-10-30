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

public class EpicsHandlerTest {
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
    public void testEpicsHandlerCreatesEpic() throws IOException, InterruptedException {
        String epicJson = "{\"name\":\"Купить конфеты\",\"description\":\"Выбрать самые вкусные\",\"duration\": 30," +
                          "\"startTime\": \"2025-10-01T09:15\"}";
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .POST(HttpRequest.BodyPublishers.ofString(epicJson))
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(201, response.statusCode(), response.body());

        // Проверяем список задач
        List<Epic> epics = taskManager.getEpicTasks();
        Assertions.assertFalse(epics.isEmpty(), "Эпики не возвращаются");
        Assertions.assertEquals(1, epics.size(), "Некорректное количество эпиков");
        Assertions.assertEquals("Купить конфеты", epics.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    public void testEpicsHandlerUpdatesEpic() throws IOException, InterruptedException {
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
        String epicJson = "{\"name\":\"Купить конфеты\",\"description\":\"Выбрать самые вкусные\",\"duration\":30," +
                "\"startTime\":\"2025-10-01T09:15\",\"id\":1}";
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(201, response.statusCode());

        // Проверяем список задач
        List<Epic> epics = taskManager.getEpicTasks();
        Assertions.assertFalse(epics.isEmpty(), "Задачи не возвращаются");
        Assertions.assertEquals(1, epics.size(), "Некорректное количество задач");
        Assertions.assertEquals("Купить конфеты", epics.getFirst().getName(), "Название задачи не изменилось");
    }

    @Test
    public void testEpicsHandlerDeletesEpic() throws IOException, InterruptedException {
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
        URI uri = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        // Проверяем список задач
        List<Epic> epics = taskManager.getEpicTasks();
        Assertions.assertTrue(epics.isEmpty(), "Эпик не удалён");
    }

    @Test
    public void testEpicsHandlerGetsEpicsById() throws IOException, InterruptedException {
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
        URI uri = URI.create("http://localhost:8080/epics/1");
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
        Assertions.assertEquals("Подготовка к Новому году", jsonElement.getAsJsonObject().get("name").getAsString(), "Отличается название");
    }

    @Test
    public void testEpicsHandlerGetsEpics() throws IOException, InterruptedException {
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
                new Epic(
                        "Подготовка к отпуску",
                        "Много разных важных дел",
                        2,
                        TaskStatus.NEW,
                        TaskType.EPIC,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00")
                )
        );
        URI uri = URI.create("http://localhost:8080/epics");
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
        Assertions.assertEquals(2, jsonElement.getAsJsonArray().size(), "Возвращено неверное число эпиков");
        Assertions.assertEquals(
                "Подготовка к отпуску",
                jsonElement.getAsJsonArray().get(1).getAsJsonObject().get("name").getAsString(),
                "Возвращено неверное название втого эпика"
        );
    }

    @Test
    public void testEpicHandlerGetsEpicSubtasks() throws IOException, InterruptedException {
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

        URI uri = URI.create("http://localhost:8080/epics/1/subtasks");
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
        Assertions.assertEquals(2, jsonElement.getAsJsonArray().size(), "Возвращено неверное число подзадач");
        Assertions.assertEquals(
                "Купить конфеты",
                jsonElement.getAsJsonArray().get(1).getAsJsonObject().get("name").getAsString(),
                "Возвращено неверное название второй подзадачи"
        );
    }

    @Test
    public void testEpicsHandlerDealsWithErrors() throws IOException, InterruptedException {
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
                new Epic(
                        "Подготовка к отпуску",
                        "Много разных важных дел",
                        2,
                        TaskStatus.NEW,
                        TaskType.EPIC,
                        30,
                        LocalDateTime.parse("2025-12-01T12:00:00")
                )
        );
        // Запрашиваем несуществующий эпик
        URI uri = URI.create("http://localhost:8080/epics/10");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        // Пытаемся удалить несуществующий эпик
        uri = URI.create("http://localhost:8080/epics/10");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        // Пытаемся обновить несуществующий эпик
        String taskJson = "{\"name\":\"Купить конфеты\",\"description\":\"Выбрать самые вкусные\",\"duration\":30," +
                "\"startTime\":\"2025-10-01T09:15\",\"id\":10}";
        uri = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());


        // Пробуем обратиться по несуществующему пути
        uri = URI.create("http://localhost:8080/epics/10/some/strange/path");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }
}




