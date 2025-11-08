package api;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpTaskServerTest {
    private final TaskManager taskManager = new InMemoryTaskManager();
    private HttpTaskServer httpTaskServer;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    public void beforeEach() throws IOException {
        httpTaskServer = new HttpTaskServer(taskManager);
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
    }

    @Test
    public void TestHttpTaskManagerStarts() throws IOException, InterruptedException {
        httpTaskServer.start();

        // Пробуем обратиться по несуществующему пути
        URI uri = URI.create("http://localhost:8080/tasks/10/some/strange/path");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void TestHttpTaskManagerStops() {
        httpTaskServer.start();
        httpTaskServer.stop();

        // Пробуем обратиться по несуществующему пути
        URI uri = URI.create("http://localhost:8080/tasks/10/some/strange/path");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(5))
                .DELETE()
                .build();

        Assertions.assertThrows(
                ConnectException.class,
                () -> httpClient.send(request, HttpResponse.BodyHandlers.ofString()),
                "Вебсервер не остановлен"
        );
    }
}