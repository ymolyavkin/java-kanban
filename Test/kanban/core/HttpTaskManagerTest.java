package kanban.core;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import kanban.taskapi.HttpTaskServer;
import kanban.taskapi.KVServer;
import kanban.taskapi.KVTaskClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpRequest;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
//class HttpTaskManagerTest extends TaskManagerTest {
    //{"0": {"title": "Физминутка", "description": "Выполнить десять приседаний", "id": 0, "status": "NEW", "duration": 15,
//    "startTime": "23.02.2023 12:24"},
//  "1": {"title": "Почитать новости", "description": "Открыть мессенджер и просмотреть новые сообщения", "id": 1, "status": "NEW", "duration": 15,
//    "startTime": "24.02.2023 12:24"}}
 //   private HttpTaskManager httpTaskManager;
    public static final int KV_PORT = 8078;
    public static final int TASK_PORT = 8080;
    private KVServer kvServer;
    private KVTaskClient kvTaskClient;
    private HttpServer httpServer;

    private URI url;
    private HttpRequest request;
    private Gson gson;

    @BeforeEach
    void init() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        System.out.println("HTTP-KV-сервер запущен на " + KV_PORT + " порту!");

        taskManager = (HttpTaskManager) Managers.getDefault();

        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(TASK_PORT), 0);

        httpServer.createContext("/tasks", new HttpTaskServer(taskManager));
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + TASK_PORT + " порту!");
        /*String url = "http://localhost:8080/tasks/";

        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            System.out.println("response body: " + response.body());

            EpicTask taskActualResult = gson.fromJson(response.body(), EpicTask.class);

            // assertEquals(epicTask, taskActualResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
    }
    @AfterEach
    void tearDown() {
        httpServer.stop(1);
        kvServer.stop();

    }

    void someMethod() {

    }
}