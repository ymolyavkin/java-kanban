package kanban.tasksAPI;

import java.net.URI;

public class KVTaskClient {
    private URI url;

    public KVTaskClient(URI url) {
        // TODO: 21.03.2023 В конструкторе нужно сделать регистрацию на сервере хранилища
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
     // TODO: 21.03.2023 add return
        return null;
    }
}
