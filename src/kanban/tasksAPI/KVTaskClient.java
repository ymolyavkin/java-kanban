package kanban.tasksAPI;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final URI url;
    private final HttpClient client;
    //private final String key;

    public KVTaskClient(URI url) {
        // TODO: 21.03.2023 В конструкторе нужно сделать регистрацию на сервере хранилища
        this.url = url;
        client = HttpClient.newHttpClient();
        // key = sendRequest(url);
        //  System.out.println("From consructor client: key = " + key);
    }

    /*public String getKey() {
        return key;
    }*/
/*
Tasks";
    private static final String KEY_EPICS = "Epics";
    private static final String KEY_SUBTASKS = "Subtasks";
    private static final String KEY_HISTORY = "History";
 */
    /**
     * Метод должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_TOKEN=.
     *
     * @param key
     * @param json
     */
    public void put(String json, String key) throws IOException, InterruptedException {
        String url="http://localhost:8078/save/";
        switch (key) {
            case "Tasks" -> {
                url += "KEY_TASK?API_TOKEN=DEBUG";
            }
            case "Epics" -> {
                url += "KEY_EPIC?API_TOKEN=DEBUG";
            }
            case "SingleEpic" -> {
                url += "KEY_SINGLE_EPIC?API_TOKEN=DEBUG";
            }
            case "History" -> {
                url += "KEY_HISTORY?API_TOKEN=DEBUG";
            }
        }
        URI allTasksUrl = URI.create(url);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(allTasksUrl).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    }



    public String load(String key) {
        //String startUrl = "http://localhost:8078/load/";
        //KEY_TASK?API_TOKEN=DEBUG

        return "Json";
    }



    public String getDataFromServer(String key) {

        String url="http://localhost:8078/load/";
        switch (key) {
            case "Tasks" -> {
                url += "KEY_TASK?API_TOKEN=DEBUG";
            }
            case "Epics" -> {
                url += "KEY_EPIC?API_TOKEN=DEBUG";
            }
            case "SingleEpic" -> {
                url += "KEY_SINGLE_EPIC?API_TOKEN=DEBUG";
            }
            case "History" -> {
                url += "KEY_HISTORY?API_TOKEN=DEBUG";
            }
        }
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

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
        return response.body();
    }

    public void restoreSingleEpic(String jsonStringSingleEpic, String keyEpics) {
    }
}
