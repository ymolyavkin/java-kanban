package kanban.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import kanban.model.AbstractTask;
import kanban.model.EpicTask;
import kanban.model.Task;
import kanban.serialization.DurationTypeAdapter;
import kanban.serialization.LocalDateTimeConverter;
import kanban.tasksAPI.KVTaskClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    private static final String KEY_TASKS = "Tasks";
    private static final String KEY_EPICS = "Epics";
    private static final String KEY_SUBTASKS = "Subtasks";
    private static final String KEY_HISTORY = "History";
    private static Gson gson;

    private KVTaskClient kvTaskClient;
    private URI url;


    public HttpTaskManager(URI url) {
        super();
        this.url = url;
        kvTaskClient = new KVTaskClient(url);

        GsonBuilder gSonBuilder = new GsonBuilder();
        //  gSonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
        //  gSonBuilder.registerTypeAdapter(Time.class, new TimeDeserializer());
        //gSonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gSonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter());
        gSonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        gSonBuilder.setPrettyPrinting();

        // gSonBuilder.excludeFieldsWithoutExposeAnnotation();
        gson = gSonBuilder.create();

        this.restoreDataFromServer();
    }

    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }

    @Override
    protected void save() {


        Map<Integer, AbstractTask> tasks = this.getStandardTasks();
        Map<Integer, AbstractTask> epics = this.getEpicTasks();

        String jsonStringTasks = gson.toJson(tasks);
        System.out.println(jsonStringTasks);

      //  Type taskMapType = new TypeToken<HashMap<Integer, Task>>() {}.getType();
      //  HashMap<Integer, Task> taskHashMap = gson.fromJson(jsonStringTasks, taskMapType);





        Task singleTask = (Task)tasks.get(0);
        String jsonStringSingleTask = gson.toJson(singleTask);


        EpicTask epic = (EpicTask) epics.get(6);

        String jsonStringEpic = gson.toJson(epic);

        Task task = gson.fromJson(jsonStringSingleTask, Task.class);
      /*  Type singtleTaskType = new TypeToken<Task>() {}.getType();
        Task task = gson.fromJson(jsonStringSingleTask, singtleTaskType);
        Type epicType = new TypeToken<EpicTask>() {}.getType();
        EpicTask epicTask = gson.fromJson(jsonStringEpic, epicType);


*/
        String jsonStringAllEpics = gson.toJson(epics);
        System.out.println(jsonStringAllEpics);

        Map<Integer, Object> myMap = new HashMap<>();
        Map<Integer, AbstractTask> myMap1 = new HashMap<>();
        Map<Integer, Task> myMap2 = new HashMap<>();

        myMap = (Map<Integer, Object>) gson.fromJson(jsonStringTasks, myMap.getClass());
        try {
            putDataToKVServer(jsonStringTasks);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String resources = getDataFromKVServer();

        System.out.println();

    }

    private String getDataFromKVServer() {
        return "da";
    }

    private void putDataToKVServer(String data) throws URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8078/save/KEY_TASKS?API_TOKEN=DEBUG"))
                // .headers("Content-Type", "text/plain;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();


        HttpClient client = HttpClient.newHttpClient();

        /*HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/


        /*// выводим код состояния и тело ответа
        System.out.println("Код состояния: " + response.statusCode());
        System.out.println("Тело ответа: " + response.body());*/

        /*String response = kvTaskClient.sendRequest(url);
        System.out.println("response = " + response);*/
        // /save/KEY_TASKS?API_TOKEN=1679727309165
        /*
         String url = "https://www.ya.ru/";

        // добавьте отлов и обработку исключений вокруг кода ниже
        URI uri = URI.create(url);

        // создаём запрос
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        // создаём HTTP-клиент
        HttpClient client = HttpClient.newHttpClient();

        // отправляем запрос
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // выводим код состояния и тело ответа
        System.out.println("Код состояния: " + response.statusCode());
        System.out.println("Тело ответа: " + response.body());
         */

    }


    public HttpTaskManager load(String key) {
        this.restoreDataFromServer();
        return this;
    }

    private void restoreDataFromServer() {
       /* gson=new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationA)*/
        save();
    }


    /*  private void put(String key, String json) {

      }*/
    private void sendRequest(String resources) {
        // используем код состояния как часть URL-адреса
        // URI uri = URI.create("http://localhost:8078/register/" + resources);
        /*URI uri = URI.create("http://localhost:8078/register/");
        KVTaskClient kvTaskClient = new KVTaskClient(uri);*/

        String response = kvTaskClient.sendRequest(url);
        System.out.println("response = " + response);
    }
   /* @Override
    public List<AbstractTask> getHistory() {
        kvTaskClient.doSomething();
        sendRequest("history");
        // TODO: 23.03.2023 add return value
        return new ArrayList<>();
    }*/

// .uri(URI.create(url + "/save/" + key + "?API_TOKEN=" + API_KEY))
}
