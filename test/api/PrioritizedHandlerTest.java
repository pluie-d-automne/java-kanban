package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.TaskStatus;
import task.TaskType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class PrioritizedHandlerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    HttpClient httpClient = HttpClient.newHttpClient();

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
    public void testPrioritizedHandlerGetsPrioritized() throws IOException, InterruptedException {
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

        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        Assertions.assertEquals(200, response.statusCode(), response.body());

        // Проверяем, что история пустая
        JsonElement jsonElement = JsonParser.parseString(response.body());
        Assertions.assertFalse(jsonElement.getAsJsonArray().isEmpty(), "Список пустой");
        Assertions.assertEquals(2, jsonElement.getAsJsonArray().size(), "В списке некорректное число задач");
        Assertions.assertEquals(
                3,
                jsonElement.getAsJsonArray().get(1).getAsJsonObject().get("id").getAsInt(),
                "Последняя по приоритету задачу не 3-я"
        );
    }


    @Test
    public void testPrioritizedHandlerDealsWithErrors() throws IOException, InterruptedException {
        // Пробуем обратиться по несуществующему пути
        URI uri = URI.create("http://localhost:8080/prioritized/some/strange/path");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }
}




