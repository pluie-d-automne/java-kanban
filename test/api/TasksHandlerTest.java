package api;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class TasksHandlerTest {
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
}


