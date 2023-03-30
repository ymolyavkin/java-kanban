package kanban.taskapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import kanban.core.HttpTaskManager;
import kanban.core.Managers;
import kanban.model.*;
import kanban.serialization.DurationTypeAdapter;
import kanban.serialization.LocalDateTimeConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    private static String kvUrl = "http://localhost:8078/";
    private static String httpUrl = "http://localhost:8080/tasks/";
    private HttpTaskManager httpTaskManager;
    private KVServer kvServer;
    private KVTaskClient kvTaskClient;
    private HttpServer httpServer;
    private static Map<Integer, AbstractTask> standardTasks = new HashMap<>();
    private static Map<Integer, AbstractTask> epicTasks = new HashMap<>();
    private static TreeSet<AbstractTask> allTasksSorted = new TreeSet<>();

    public static final int KV_PORT = 8078;
    public static final int TASK_PORT = 8080;
    final String[] parts = new String[]{"Title", "Description", "21.03.2021 12:00", "PT15M"};
    final String title = parts[0];
    final String description = parts[1];
    final int id = 0;

    final int parentId = 1;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    LocalDateTime startTime = LocalDateTime.parse(parts[2], formatter);
    Duration duration = Duration.parse(parts[3]);
    Task task;
    Subtask subtask;
    EpicTask epic;

    @BeforeEach
    void init() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        System.out.println("HTTP-KV-сервер запущен на " + KV_PORT + " порту!");

        httpTaskManager = (HttpTaskManager) Managers.getDefault();

        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(TASK_PORT), 0);

        httpServer.createContext("/tasks", new HttpTaskServer(httpTaskManager));
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + TASK_PORT + " порту!");


        task = new Task(title, description, id, startTime, duration);
        epic = new EpicTask(title, description, id);
        subtask = new Subtask(title, description, id, parentId, startTime, duration);
        subtask.setStatus(Status.NEW);
        epic.addSubtask(subtask);
    }

    @AfterEach
    void tearDown() {
        httpServer.stop(1);
        kvServer.stop();
    }

    @Test
    void handle() {
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/alltasks");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("response: " + response);
    }

    @Test
    void createTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/addtask");

        Gson gson;
        GsonBuilder gSonBuilder = new GsonBuilder();

        gSonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter());
        gSonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        //gSonBuilder.setPrettyPrinting();


        gson = gSonBuilder.create();
        String json = gson.toJson(task);
        System.out.println("json: " + json);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("response: " + response);
    }

    @Test
    void createTasksToKV() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8078/save/KEY_SINGLE_TASK?API_TOKEN=DEBUG");

        Gson gson;
        GsonBuilder gSonBuilder = new GsonBuilder();

        gSonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter());
        gSonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        gSonBuilder.setPrettyPrinting();


        gson = gSonBuilder.create();
        String json = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("response: " + response);
    }

    @Test
    @DisplayName("Подзадача - Delete с некорректным - 404")
    void shouldDeleteSubtaskWithInvalidId() throws IOException, InterruptedException {
        httpTaskManager.addEpic(epic);
        httpTaskManager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        //kvTaskClient = new KVTaskClient(httpUrl);
        URI testUrl = URI.create(httpUrl + "task/?id=-1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(testUrl)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Not Found", response.body());
    }
}


