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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class HttpTaskManager extends FileBackedTasksManager {
    private static final String KEY_TASKS = "Tasks";
    private static final String KEY_EPICS = "Epics";
    private static final String KEY_PRIORITIZED = "Prioritized";
    private static final String KEY_HISTORY = "History";
    private static Gson gson;
    private boolean needSendToServer;

    private final KVTaskClient kvTaskClient;

    private URI url;


    public HttpTaskManager(URI url) {
        super();
        this.url = url;
        kvTaskClient = new KVTaskClient(url);
        needSendToServer = false;

        GsonBuilder gSonBuilder = new GsonBuilder();

        gSonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter());
        gSonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        gSonBuilder.setPrettyPrinting();

        // gSonBuilder.excludeFieldsWithoutExposeAnnotation();
        gson = gSonBuilder.create();

        //  restoreDataFromServer();


    }

    public void setNeedSendToServer(boolean needSendToServer) {
        this.needSendToServer = needSendToServer;
    }

    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }

    @Override
    protected void save() {
        Map<Integer, AbstractTask> tasks = getStandardTasks();
        Map<Integer, AbstractTask> epics = getEpicTasks();
        TreeSet<AbstractTask> allTasksSorted = getPrioritizedTasks();
        List<AbstractTask> history = getHistory();
        try {
            sendTasksToKV(tasks);
            sendEpicsToKV(epics);
            sendPrioritizedToKV(allTasksSorted);
            sendHistoryToKV(history);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //getAndSendEpics(epics);

        // sendRequest("This is Test");


    }

    private void sendTasksToKV(Map<Integer, AbstractTask> tasks) throws IOException, InterruptedException {
        String jsonStringTasks = gson.toJson(tasks);

        kvTaskClient.put(jsonStringTasks, KEY_TASKS);
    }
    private void sendEpicsToKV(Map<Integer, AbstractTask> epics) throws IOException, InterruptedException {
        String jsonStringEpics = gson.toJson(epics);

        kvTaskClient.put(jsonStringEpics, KEY_EPICS);
    }
    private void sendPrioritizedToKV(TreeSet<AbstractTask> prioritized) throws IOException, InterruptedException {
        List<Integer> idPrioritizedTask = new ArrayList<>(prioritized.size());
        for (AbstractTask abstractTask : prioritized) {
            idPrioritizedTask.add(abstractTask.getId());
        }
        String jsonIdPrioritized = gson.toJson(idPrioritizedTask);

        kvTaskClient.put(jsonIdPrioritized, KEY_PRIORITIZED);
    }
    private void sendHistoryToKV(List<AbstractTask> history) throws IOException, InterruptedException {
        List<Integer> idHistoryTask = new ArrayList<>(history.size());
        for (AbstractTask abstractTask : history) {
            idHistoryTask.add(abstractTask.getId());
        }
        String jsonIdHistory = gson.toJson(idHistoryTask);

        kvTaskClient.put(jsonIdHistory, KEY_HISTORY);
    }

   /* private void getAndSendTasks(Map<Integer, AbstractTask> tasks) throws IOException, InterruptedException {
        String jsonStringTasks = gson.toJson(tasks);
        System.out.println(jsonStringTasks);
        //  System.out.println();

        sendRequest(jsonStringTasks);
        Type taskMapType = new TypeToken<HashMap<Integer, Task>>() {
        }.getType();
        HashMap<Integer, Task> taskHashMap = gson.fromJson(jsonStringTasks, taskMapType);

        //  System.out.println(taskHashMap);
        //  System.out.println();
    }*/

   /* private void getAndSendEpics(Map<Integer, AbstractTask> epics) {

        String jsonStringEpics = gson.toJson(epics);
        //   System.out.println(jsonStringEpics);
        Type epicMapType = new TypeToken<HashMap<Integer, EpicTask>>() {
        }.getType();
        HashMap<Integer, EpicTask> epicTaskHashMap = gson.fromJson(jsonStringEpics, epicMapType);

        //   System.out.println(epicTaskHashMap);
        //   System.out.println();
    }*/

    private String getDataFromKVServer() {
        return "da";
    }

    /*private void putDataToKVServer(String data) throws URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8078/save/KEY_TASKS?API_TOKEN=DEBUG"))
                // .headers("Content-Type", "text/plain;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();
    }*/


    public HttpTaskManager load(String key) {
        restoreDataFromServer();
        return this;
    }

    public void restoreDataFromServer() {
        String jsonStandardTasks = kvTaskClient.getStandardTasksFromServer();
        if (jsonStandardTasks.isBlank() || jsonStandardTasks.equals("response From KVserver: Хранилище пусто")) {
            return;
        }
        Type taskMapType = new TypeToken<HashMap<Integer, Task>>() {}.getType();
        HashMap<Integer, Task> taskHashMap = gson.fromJson(jsonStandardTasks, taskMapType);
        //deleteAllTasks();
        for (Task task : taskHashMap.values()) {
            addTask(task);
        }
        System.out.println("httpTaskManager/restoreDataFromServer(): " + jsonStandardTasks);
        //save();
    }


    /*  private void put(String key, String json) {

      }*/
    /*private void sendRequest(String resources) throws IOException, InterruptedException {

        String url = "http://localhost:8080/tasks/addtask";

        // добавьте отлов и обработку исключений вокруг кода ниже
        URI uri = URI.create(url);


        kvTaskClient.sendDataToStorage(resources);

    }*/
   /* @Override
    public List<AbstractTask> getHistory() {
        kvTaskClient.doSomething();
        sendRequest("history");
        // TODO: 23.03.2023 add return value
        return new ArrayList<>();
    }*/

    @Override
    public void addEpic(EpicTask epicTask) {
        epicTask.calculateTime();
        super.addEpic(epicTask);
        if (needSendToServer) {
            save();
            needSendToServer = false;
        }
    }

    @Override
    public void addTask(Task task) {
        System.out.println("addTask ");
        super.addTask(task);
        if (needSendToServer) {
            save();
            needSendToServer = false;
        }
    }

    @Override
    public boolean updateStandardTask(Task task, String[] newTitleAndDescription, String[] newTime, boolean mustChangeStatus) {
        boolean result = super.updateStandardTask(task, newTitleAndDescription, newTime, mustChangeStatus);
        save();
        needSendToServer = false;
        return result;
    }

    @Override
    public AbstractTask findTaskByIdOrNull(int id, boolean savedToHistory) {
        var foundTask = super.findTaskByIdOrNull(id, savedToHistory);

        needSendToServer = true;
        save();
        needSendToServer = false;

        return foundTask;
    }

    @Override
    public boolean deleteTaskById(int id) {
        boolean oneTaskWasDeleted = super.deleteTaskById(id);
        save();
        needSendToServer = false;

        return oneTaskWasDeleted;
    }

    @Override
    public boolean deleteAllTasks() {
        boolean wasDeleted = super.deleteAllTasks();
        save();
        needSendToServer = false;

        return wasDeleted;
    }

// .uri(URI.create(url + "/save/" + key + "?API_TOKEN=" + API_KEY))
}
