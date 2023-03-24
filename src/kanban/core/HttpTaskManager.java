package kanban.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import kanban.model.AbstractTask;
import kanban.model.Task;
import kanban.serialization.DurationTypeAdapter;
import kanban.serialization.LocalDateTimeConverter;
import kanban.tasksAPI.KVTaskClient;

import java.lang.reflect.Type;
import java.net.URI;
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

        gson = gSonBuilder.create();

        this.restoreDataFromServer();
    }

    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }

    @Override
    protected void save() {
        Map<Integer, AbstractTask> tasks = this.getStandardTasks();

        String jsonString = gson.toJson(tasks);
        System.out.println(jsonString);

        Type type = new TypeToken<Map<Integer, Task>>(){}.getType();

        Map<Integer,Object> myMap = new HashMap<Integer,Object>();
        myMap = (Map<Integer,Object>) gson.fromJson(jsonString, myMap.getClass());
        /*Type typeMapStandardTask = new TypeToken<HashMap<Integer, Task>>() {
        }.getType();*/
        Type typeStandardTask = new TypeToken<Task>() {
        }.getType();
       /* HashMap<Integer, Task> clonedMap = gson.fromJson(jsonString, typeMapStandardTask);*/
        System.out.println("Name : " + myMap.get("0"));
       /* var v = myMap.get("0").toString();
        Task task = gson.fromJson(v, typeStandardTask);*/
       /* System.out.println("Mobile : " + userData.get("Mobile"));
        System.out.println("Designation : " + userData.get("Designation"));
        System.out.println("Pet : " + userData.get("Pet"));
        System.out.println("Address : " + userData.get("Address"));*/

        System.out.println();

    }
    /*
    Map<String, File> myMap;
Type typeOfMap = new TypeToken<Map<String, File>>() { }.getType();
Gson gson = new GsonBuilder().create();
String json = gson.toJson(myMap, typeOfMap);
     */


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
