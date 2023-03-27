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
    private static final String KEY_SINGLE_EPIC = "SingleEpic";
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
        EpicTask epic = (EpicTask) epics.get(2);
        try {
            sendTasksToKV(tasks);
            sendEpicsToKV(epics);
            sendPrioritizedToKV(allTasksSorted);
            sendHistoryToKV(history);
            sendSingleEpicToKV(epic);
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
    private void sendSingleEpicToKV(EpicTask epic) throws IOException, InterruptedException {
        String jsonStringSingleEpic = gson.toJson(epic);

        kvTaskClient.put(jsonStringSingleEpic, KEY_SINGLE_EPIC);
        //kvTaskClient.restoreSingleEpic(jsonStringSingleEpic, KEY_SINGLE_EPIC);
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





    public HttpTaskManager load(String key) {
        restoreStandardTasksFromServer();
        restoreEpicsFromServer();
        restoreSingleEpicFromServer();
        restoreHistoryFromServer();
        //отсортированный treeSet задач должен создаться попутно
        return this;
    }

    public void restoreStandardTasksFromServer() {
        String jsonStandardTasks = kvTaskClient.load("Tasks");
        if (jsonStandardTasks.isBlank() || jsonStandardTasks.equals("response From KVserver: Хранилище пусто")) {
            return;
        }
        Type taskMapType = new TypeToken<HashMap<Integer, Task>>() {}.getType();
        HashMap<Integer, Task> taskHashMap = gson.fromJson(jsonStandardTasks, taskMapType);

        for (Task task : taskHashMap.values()) {
            addTask(task);
        }
        //System.out.println("httpTaskManager/restoreDataFromServer(): " + jsonStandardTasks);
    }
    public void restoreSingleEpicFromServer() {
        String jsonSingleEpic = kvTaskClient.load("SingleEpic");
        if (jsonSingleEpic.isBlank() || jsonSingleEpic.equals("response From KVserver: Хранилище пусто")) {
            return;
        }
        Type epicTaskType = new TypeToken<EpicTask>() {}.getType();
        EpicTask epic = gson.fromJson(jsonSingleEpic, epicTaskType);
        System.out.println(jsonSingleEpic);
        addEpic(epic);
        //System.out.println("httpTaskManager/restoreDataFromServer(): " + jsonStandardTasks);
    }
    public void restoreEpicsFromServer() {
        String jsonEpics = kvTaskClient.load("Epics");
        if (jsonEpics.isBlank() || jsonEpics.equals("response From KVserver: Хранилище пусто")) {
            return;
        }
        Type epicMapType = new TypeToken<HashMap<Integer, EpicTask>>() {}.getType();
        HashMap<Integer, EpicTask> epicHashMap = gson.fromJson(jsonEpics, epicMapType);
        System.out.println();

        for (EpicTask epic : epicHashMap.values()) {
            addEpic(epic);
        }
        //System.out.println("httpTaskManager/restoreDataFromServer(): " + jsonStandardTasks);
    }
    public void restoreHistoryFromServer() {

        String jsonHistory = kvTaskClient.load("History");
        if (jsonHistory.isBlank() || jsonHistory.equals("response From KVserver: Хранилище пусто")) {
            return;
        }
        Type historyListType = new TypeToken<ArrayList<Integer>>() {}.getType();
        ArrayList<Integer> history = gson.fromJson(jsonHistory, historyListType);

        for (int id : history) {
            findTaskByIdOrNull(id, true);
        }
    }


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
        //save();
        needSendToServer = false;

        return wasDeleted;
    }

// .uri(URI.create(url + "/save/" + key + "?API_TOKEN=" + API_KEY))
}
