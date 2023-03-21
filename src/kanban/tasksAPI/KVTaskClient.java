package kanban.tasksAPI;

import java.net.URL;

public class KVTaskClient {
    private URL url;

    public KVTaskClient(URL url) {
        this.url = url;
    }

    /**
     * Метод должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_TOKEN=.
     * @param key
     * @param json
     */
    private void put(String key, String json) {

    }

    /**
     * Метод должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_TOKEN=.
     * @param key
     * @return
     */
    private String load(String key) {

    }
}
