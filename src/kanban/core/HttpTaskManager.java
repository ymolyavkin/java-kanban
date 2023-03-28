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
    private static final String KEY_PRIORITIZED = "prioritized";
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
       // gSonBuilder.setPrettyPrinting();

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
            if (!tasks.isEmpty()) {
                sendTasksToKV(tasks);
            }
            if (!epics.isEmpty()) {
                sendEpicsToKV(epics);
            }
            if (!allTasksSorted.isEmpty()) {
                sendPrioritizedToKV(allTasksSorted);
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

    public HttpTaskManager load(String key) {
        restoreStandardTasksFromServer();
        restoreEpicsFromServer();
        //restoreSingleEpicFromServer();
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
        //System.out.println("httpTaskManager/restoreDataFromServer(): " + jsonStandardTasks);
    }

    public void restoreSingleEpicFromServer() {
        String jsonSingleEpic = kvTaskClient.load(KEY_SINGLE_EPIC);
        if (jsonSingleEpic.isBlank() || jsonSingleEpic.equals("response From KVserver: Хранилище пусто")) {
            return;
        }
        Type epicTaskType = new TypeToken<EpicTask>() {
        }.getType();
        EpicTask epic = gson.fromJson(jsonSingleEpic, epicTaskType);
        System.out.println(jsonSingleEpic);
        addEpic(epic);
        //System.out.println("httpTaskManager/restoreDataFromServer(): " + jsonStandardTasks);
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
        //System.out.println("httpTaskManager/restoreDataFromServer(): " + jsonStandardTasks);
    }

    public void restoreHistoryFromServer() {

        String jsonHistory = kvTaskClient.load(KEY_HISTORY);
        if (jsonHistory.isBlank() || jsonHistory.equals("response From KVserver: Хранилище пусто")) {
            return;
        }
        Type historyListType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        ArrayList<Integer> history = gson.fromJson(jsonHistory, historyListType);

        for (int id : history) {
            findTaskByIdOrNull(id, true);
        }
    }


    @Override
    public void addEpic(EpicTask epicTask) {
        epicTask.calculateTime();
        super.addEpic(epicTask);
       /* if (needSendToServer) {
            save();
            needSendToServer = false;
        }*/
        save();
    }

    @Override
    public void addTask(Task task) {
        System.out.println("addTask ");
        super.addTask(task);
        /*if (needSendToServer) {
            save();
            needSendToServer = false;
        }*/
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

        /*needSendToServer = true;
        save();
        needSendToServer = false;*/
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
        save();
        needSendToServer = false;

        return wasDeleted;
    }

    // .uri(URI.create(url + "/save/" + key + "?API_TOKEN=" + API_KEY))
    //http://localhost:8080/tasks/addtask
    //Task:
    //{"title":"Физминутка1","description":"Выполнить упражнения1","id":10,"status":"NEW","duration":25,"startTime":"23.03.2023 12:24"}
    //Правильный эпик:
    //{"subtasks":[{"parentId":2,"title":"Подзадача 1","description":"Прочитать ТЗ","id":3,"status":"NEW","duration":15,"startTime":"26.02.2023 12:20"},{"parentId":2,"title":"Подзадача 2","description":"Понять ТЗ","id":4,"status":"NEW","duration":15,"startTime":"27.02.2023 12:39"}],"endTime":"27.02.2023 12:54","title":"Понять условие домашнего задания","description":"Понять как сделать рефакторинг проекта \u0027Трекер задач\u0027 в соответствии с новым ТЗ","id":2,"status":"NEW","duration":30,"startTime":"26.02.2023 12:20"}
}
