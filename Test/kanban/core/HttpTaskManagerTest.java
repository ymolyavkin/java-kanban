package kanban.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import kanban.model.Task;
import kanban.serialization.DurationTypeAdapter;
import kanban.serialization.LocalDateTimeConverter;
import kanban.taskapi.HttpTaskServer;
import kanban.taskapi.KVServer;
import kanban.taskapi.KVTaskClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    public static final int KV_PORT = 8078;
    public static final int TASK_PORT = 8080;
    private KVServer kvServer;

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

    }
    @AfterEach
    void tearDown() {
        httpServer.stop(1);
        kvServer.stop();

    }
    @Test
    void restoreTaskFromJson(){
        GsonBuilder gSonBuilder = new GsonBuilder();

        gSonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter());
        gSonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        gson = gSonBuilder.create();

        String taskJson = "{\"0\":{\"title\":\"Физминутка\",\"description\":\"Выполнить упражнения\",\"id\":0,\"status\""
                + ":\"NEW\",\"duration\":15,\"startTime\":\"23.02.2023 12:24\"},\"1\":{\"title\":\"Почитать новости\","
                + "\"description\":\"Открыть мессенджер\",\"id\":1,\"status\":\"NEW\",\"duration\":15,\"startTime\":\"24.02.2023 12:24\"}}";
        Type taskType = new TypeToken<HashMap<Integer, Task>>() {
        }.getType();
        HashMap<Integer, Task> actualTask = gson.fromJson(taskJson, taskType);
        Task task = actualTask.get(0);

        assertEquals(task.getTitle(), "Физминутка");
    }
}