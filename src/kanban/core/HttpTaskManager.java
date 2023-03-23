package kanban.core;

import kanban.model.AbstractTask;
import kanban.tasksAPI.KVTaskClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private KVTaskClient kvTaskClient;
    private URI url;
    //private String key;
    public HttpTaskManager(URI url) {
        this.url=url;
        kvTaskClient=new KVTaskClient(url);
       // this.key= kvTaskClient.getKey();
    }

    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }

    @Override
    protected void save() {

    }

    /**
     * Метод должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_TOKEN=.
     *
     * @param key
     * @return
     */
    public HttpTaskManager load(String key) {
        //HttpTaskManager httpTaskManager = (HttpTaskManager) Managers.getDefault();
        //httpTaskManager.restoreDataFromServer();
        this.restoreDataFromServer();

        return this;
    }

    private void restoreDataFromServer() {
    }

    /**
     * Метод должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_TOKEN=.
     *
     * @param key
     * @param json
     */
    private void put(String key, String json) {

    }
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
