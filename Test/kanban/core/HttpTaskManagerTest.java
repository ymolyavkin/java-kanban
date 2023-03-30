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
        taskManager.deleteAllTasks();
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

    /*@Test
    void restoreStandardTaskWithId() {
        *//*LocalDateTime dateTime = LocalDateTime.of(2023, Month.FEBRUARY, 01, 8, 0);
        Status status = Status.IN_PROGRESS;
        Task task = taskManager.createStandardTask("title|description|01.01.2023 08:00|15");

        assertNotNull(task);
        assertEquals("title", task.getTitle());
        assertEquals("description", task.getDescription());

        assertEquals(Duration.parse("PT15M"), task.getDuration());
        assertEquals(Status.NEW, task.getStatus());

        if (task.getStatus() != status) {
            task.setStatus(status);
        }
        assertEquals(Status.IN_PROGRESS, task.getStatus());*//*
    }*/
    /*@Test
    void shouldGetMessageAboutOverlapTasksWhenCreateTasks() {
        Task firstTask = taskManager.createStandardTask("Title|Description|01.01.2023 08:00|20");
        Task secondTask = taskManager.createStandardTask("Title|Description|01.01.2023 08:10|20");

        assertNull(secondTask);
        String titleAndDescription = title + "|" + description;
        EpicTask epicTask = taskManager.createEpic(titleAndDescription);

        String titleAndDescriptionSubtask = title + "|" + description + "|01.01.2023 07:50|15";
        Subtask subtask = taskManager.createSubtask(titleAndDescriptionSubtask, epicTask.getId());

        assertNull(subtask);
    }*/
}