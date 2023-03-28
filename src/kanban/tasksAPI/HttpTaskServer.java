package kanban.tasksAPI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import kanban.core.HttpTaskManager;
import kanban.model.AbstractTask;
import kanban.model.EpicTask;
import kanban.model.Task;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static java.nio.charset.StandardCharsets.UTF_8;
import static kanban.tasksAPI.Endpoint.*;

public class HttpTaskServer implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Charset CP1251_CHARSET = Charset.forName("Cp1251");
    //private static final HttpTaskManager httpTaskManager = (HttpTaskManager) Managers.getDefault();
    // TODO: 22.03.2023 get key
    //private static final HttpTaskManager httpTaskManager = HttpTaskManager.load("");
    private HttpTaskManager httpTaskManager;

    public HttpTaskServer(HttpTaskManager httpTaskManager) {
        this.httpTaskManager = httpTaskManager;
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "answer";

        // извлеките метод из запроса
        String method = httpExchange.getRequestMethod();

        URI requestURI = httpExchange.getRequestURI();
        // Из экземпляра URI получить requestPath.
        String requestPath = requestURI.getPath();

        // получите информацию об эндпоинте, к которому был запрос
        Endpoint endpoint = getEndpoint(requestPath, method);
        System.out.println("endpoint: " + endpoint);


        switch (endpoint) {
            case GET_HISTORY -> {
                writeResponse(httpExchange, "Получен запрос на получение истории задач", 200);
                List<AbstractTask> historyTask = httpTaskManager.getHistory();
                System.out.println("Test exist httpTaskManager");
                //sendRequest("history");
            }
            case GET_ALL_TASKS -> {
                writeResponse(httpExchange, "Получен запрос на получение списка всех задач", 200);
                Map<Integer, AbstractTask> standardTasks = httpTaskManager.getStandardTasks();
                Map<Integer, AbstractTask> epics = httpTaskManager.getEpicTasks();
                System.out.println("Test get standard tasks" + standardTasks + " " + epics);
            }
            case GET_STANDARD_TASKS -> {
                writeResponse(httpExchange, "Получен запрос на получение обычных задач", 200);
            }
            case GET_EPIC_TASKS -> {
                writeResponse(httpExchange, "Получен запрос на получение эпиков", 200);
            }
            case GET_PRIORITIZED_TASKS -> {
                writeResponse(httpExchange, "Получен запрос на получение упорядоченного по времени списка задач", 200);
                TreeSet<AbstractTask> apiTasks = httpTaskManager.getPrioritizedTasks();
                System.out.println("apiTasks" + apiTasks);
            }
            case GET_FIND_TASK_BY_ID -> {
                writeResponse(httpExchange, "Получен запрос на получение задачи по id", 200);
            }
            case POST_ADD_EPIC -> {
                System.out.println("Ендпоинт POST_ADD_EPIC");
                // извлекаем тело запроса
                InputStream inputStream = httpExchange.getRequestBody();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String body = bufferedReader.readLine();
                System.out.println("body" + body);

                EpicTask epic = httpTaskManager.restoreEpicFromJson(body);
                httpTaskManager.addEpic(epic);

                writeResponse(httpExchange, "Получен запрос на добавление эпика", 200);
            }
            case POST_ADD_TASK -> {
                System.out.println("Ендпоинт POST_ADD_TASK");
                // извлекаем тело запроса
                InputStream inputStream = httpExchange.getRequestBody();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String body = bufferedReader.readLine();
                System.out.println("body" + body);

                Task task = httpTaskManager.restoreTaskFromJson(body);
                httpTaskManager.addTask(task);

                writeResponse(httpExchange, "Получен запрос на добавление задачи", 200);
            }
            case POST_ADD_PRIORITIZED -> {
                writeResponse(httpExchange, "Получен запрос на добавление отсортированного списка", 200);
            }
            case POST_ADD_HISTORY -> {
                writeResponse(httpExchange, "Получен запрос на добавление истории просмотров", 200);
            }
            case DELETE_DELETE_TASK_BY_ID -> {
                int id = 0; //requestPath
                if (id != -1) {
                    httpTaskManager.deleteTaskById(id);
                    System.out.println("Удалена задача с id " + id);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Получен некорректный id: " + id);
                    httpExchange.sendResponseHeaders(405, 0);
                }
                writeResponse(httpExchange, "Получен запрос на удаление задачи по id", 200);
            }
            case DELETE_DELETE_ALL_TASKS -> {
                writeResponse(httpExchange, "Получен запрос на удаление всех задач", 200);
            }
            default -> writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
        }
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }
    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        // реализуйте этот метод

        String[] splitStrings = requestPath.split("/");
        String lastPart = splitStrings[splitStrings.length - 1];
        switch (requestMethod) {
            case "GET" -> {
                switch (lastPart) {
                    case "history" -> {
                        return GET_HISTORY;
                    }
                    case "alltasks" -> {
                        return GET_ALL_TASKS;
                    }
                    case "standardtasks" -> {
                        return GET_STANDARD_TASKS;
                    }
                    case "epics" -> {
                        return GET_EPIC_TASKS;
                    }
                    case "prioritizedtasks" -> {
                        return GET_PRIORITIZED_TASKS;
                    }
                    case "findtaskbyid" -> {
                        return GET_FIND_TASK_BY_ID;
                    }
                }
            }
            case "POST" -> {
                switch (lastPart) {
                    case "addepic" -> {
                        return POST_ADD_EPIC;
                    }
                    case "addtask" -> {
                        return POST_ADD_TASK;
                    }
                    case "addprioritized" -> {
                        return POST_ADD_PRIORITIZED;
                    }
                    case "addhistory" -> {
                        return POST_ADD_HISTORY;
                    }
                }
            }
            case "DELETE" -> {
                switch (lastPart) {
                    case "deletetaskbyid" -> {
                        return DELETE_DELETE_TASK_BY_ID;
                    }
                    case "deletealltasks" -> {
                        return DELETE_DELETE_ALL_TASKS;
                    }
                }
            }
            default -> {
                return UNKNOWN;
            }
        }
        return UNKNOWN;
    }


    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {

        exchange.sendResponseHeaders(responseCode, 0);

        if (!responseString.isEmpty()) {
            try (OutputStream os = exchange.getResponseBody()) {
                //os.write(responseString.getBytes(CP1251_CHARSET));
                os.write(responseString.getBytes(DEFAULT_CHARSET));
            }
        }
    }

  /*  private void sendRequest(String resources) {
        // используем код состояния как часть URL-адреса
       // URI uri = URI.create("http://localhost:8078/register/" + resources);
        URI uri = URI.create("http://localhost:8078/register/");
        KVTaskClient kvTaskClient = new KVTaskClient(uri);
        String response = kvTaskClient.sendRequest(uri);
        System.out.println("response = " + response);
    }*/
}
