package kanban.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import kanban.model.AbstractTask;
import kanban.model.EpicTask;
import kanban.model.Task;
import kanban.serialization.DurationTypeAdapter;
import kanban.serialization.LocalDateTimeConverter;
import kanban.taskapi.KVTaskClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class HttpTaskManager extends FileBackedTasksManager {
    private static final String KEY_TASKS = "tasks";
    private static final String KEY_EPICS = "epics";
    private static final String KEY_HISTORY = "history";
    private static final String KEY_SINGLE_EPIC = "singleepic";
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

        gson = gSonBuilder.create();
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
            if (!tasks.isEmpty()) {
                sendTasksToKV(tasks);
            }
            if (!epics.isEmpty()) {
                sendEpicsToKV(epics);
            }
            if (!history.isEmpty()) {
                sendHistoryToKV(history);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendTasksToKV(Map<Integer, AbstractTask> tasks) throws IOException, InterruptedException {
        String jsonStringTasks = gson.toJson(tasks);

        kvTaskClient.put(jsonStringTasks, KEY_TASKS);
    }

    private void sendEpicsToKV(Map<Integer, AbstractTask> epics) throws IOException, InterruptedException {
        String jsonStringEpics = gson.toJson(epics);

        kvTaskClient.put(jsonStringEpics, KEY_EPICS);
    }

    private void sendHistoryToKV(List<AbstractTask> history) throws IOException, InterruptedException {
        List<Integer> idHistoryTask = new ArrayList<>(history.size());
        for (AbstractTask abstractTask : history) {
            idHistoryTask.add(abstractTask.getId());
        }
        String jsonIdHistory = gson.toJson(idHistoryTask);

        kvTaskClient.put(jsonIdHistory, KEY_HISTORY);
    }

    public Task restoreTaskFromJson(String json) {
        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task task = gson.fromJson(json, taskType);

        return task;
    }

    public EpicTask restoreEpicFromJson(String json) {
        Type epicTaskType = new TypeToken<EpicTask>() {
        }.getType();
        EpicTask epic = gson.fromJson(json, epicTaskType);

        return epic;
    }

    public HttpTaskManager load() {
        restoreStandardTasksFromServer();
        restoreEpicsFromServer();
        restoreHistoryFromServer();
        //отсортированный treeSet задач должен создаться попутно
        return this;
    }

    public void restoreStandardTasksFromServer() {
        String jsonStandardTasks = kvTaskClient.load(KEY_TASKS);
        if (jsonStandardTasks.isBlank() || jsonStandardTasks.equals("response From KVserver: Хранилище пусто")) {
            return;
        }
        Type taskMapType = new TypeToken<HashMap<Integer, Task>>() {
        }.getType();
        HashMap<Integer, Task> taskHashMap = gson.fromJson(jsonStandardTasks, taskMapType);

        for (Task task : taskHashMap.values()) {
            addTask(task);
        }
    }

    public void restoreEpicsFromServer() {
        String jsonEpics = kvTaskClient.load(KEY_EPICS);
        if (jsonEpics.isBlank() || jsonEpics.equals("response From KVserver: Хранилище пусто")) {
            return;
        }
        Type epicMapType = new TypeToken<HashMap<Integer, EpicTask>>() {
        }.getType();
        HashMap<Integer, EpicTask> epicHashMap = gson.fromJson(jsonEpics, epicMapType);
        System.out.println();

        for (EpicTask epic : epicHashMap.values()) {
            addEpic(epic);
        }
    }

    public void restoreHistoryFromServer() {

        String jsonHistory = kvTaskClient.load(KEY_HISTORY);
        if (!jsonHistory.startsWith("{") || jsonHistory.isBlank() || jsonHistory.equals("response From KVserver: Хранилище пусто")) {
            return;
        }
        Type historyListType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        ArrayList<Integer> history = gson.fromJson(jsonHistory, historyListType);

        for (int id : history) {
            findTaskByIdOrNull(id, true);
        }
    }

    public String objectToJson(Object object) {
        return gson.toJson(object);
    }

    public String abstractTaskToJson(AbstractTask abstractTask) {
        return gson.toJson(abstractTask);
    }

    @Override
    public void addEpic(EpicTask epicTask) {
        epicTask.calculateTime();
        super.addEpic(epicTask);

        save();
    }

    @Override
    public void addTask(Task task) {
        System.out.println("addTask ");
        super.addTask(task);

        save();
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

        if (savedToHistory) {
            save();
        }
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
        kvTaskClient.clearStorage();

        return wasDeleted;
    }

}
