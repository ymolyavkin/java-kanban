package kanban.core;

import com.google.gson.Gson;
import kanban.model.EpicTask;
import kanban.tasksAPI.KVTaskClient;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest {
//{"0": {"title": "Физминутка", "description": "Выполнить десять приседаний", "id": 0, "status": "NEW", "duration": 15,
//    "startTime": "23.02.2023 12:24"},
//  "1": {"title": "Почитать новости", "description": "Открыть мессенджер и просмотреть новые сообщения", "id": 1, "status": "NEW", "duration": 15,
//    "startTime": "24.02.2023 12:24"}}

    private KVTaskClient kvTaskClient;
    private URI url;
    private HttpRequest request;
    private Gson gson;

    @BeforeEach
    void setUp() {
        String url = "https://www.ya.ru/";

        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            request=HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response=httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            System.out.println("response body: " + response.body());

            EpicTask taskActualResult=gson.fromJson(response.body(), EpicTask.class);

           // assertEquals(epicTask, taskActualResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void someMethod() {

    }
}