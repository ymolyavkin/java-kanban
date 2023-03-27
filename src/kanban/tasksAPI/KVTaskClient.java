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
    public void sendDataToStorage(String json, String key) throws IOException, InterruptedException {
        String url="http://localhost:8078/save/";
        switch (key) {
            case "Tasks" -> {
                url += "KEY_TASK?API_TOKEN=DEBUG";
            }
            case "Epics" -> {
                url += "KEY_EPIC?API_TOKEN=DEBUG";
            }
            case "Prioritized" -> {
                url += "addprioritized";
            }
            case "History" -> {
                url += "addhistory";
            }
        }
        URI allTasksUrl = URI.create(url);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(allTasksUrl).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    }

    public String sendRequest(URI uri) {
        String answer = "";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString("Sample request body"))
                .build();

        //   HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // обработайте указанные в задании коды состояния
            int status = response.statusCode();
            switch (status) {
                case 400:
                    answer = "В запросе содержится ошибка. Проверьте параметры и повторите запрос.";
                    break;
                case 404:
                    answer = "По указанному адресу нет ресурса. Проверьте URL-адрес ресурса и повторите запрос.";
                    break;
                case 500:
                    answer = "На стороне сервера произошла непредвиденная ошибка.";
                    break;
                case 503:
                    answer = "Сервер временно недоступен. Попробуйте повторить запрос позже.";
                    break;
                default:
                    answer = response.statusCode() + response.body();
            }

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            answer += "Во время выполнения запроса ресурса по url-адресу: '" + uri + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.";
        }
        return answer;
    }

    /**
     * Метод должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_TOKEN=.
     *
     * @param key
     * @param json
     */
    public void put(String key, String json) {

    }

    public String load(String key) {
        return "Json";
    }

    public void doSomething() {
        System.out.println("Do Something");
    }

    public String getStandardTasksFromServer() {
        String url = "http://localhost:8078/load/KEY_TASK?API_TOKEN=DEBUG";
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
}
