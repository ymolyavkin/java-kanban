package kanban.core;

import kanban.tasksAPI.KVTaskClient;

import java.net.URI;

public class HttpTaskManager extends FileBackedTasksManager {
    public HttpTaskManager(URI url) {
        KVTaskClient kvTaskClient = new KVTaskClient(url);
    }
    @Override
    protected void save(){

    }
    /**
     * Метод должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_TOKEN=.
     * @param key
     * @return
     */
    public static HttpTaskManager load(String key) {
        HttpTaskManager httpTaskManager = (HttpTaskManager) Managers.getDefault();
        httpTaskManager.restoreDataFromServer();

        return httpTaskManager;
    }

    private void restoreDataFromServer() {
    }

    /**
     * Метод должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_TOKEN=.
     * @param key
     * @param json
     */
    private void put(String key, String json) {

    }


}
