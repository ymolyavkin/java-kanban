package kanban.tasksAPI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import kanban.core.HttpTaskManager;
import kanban.core.Managers;
import kanban.model.AbstractTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static kanban.tasksAPI.Endpoint.*;

public class HttpTaskServer implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Charset CP1251_CHARSET = Charset.forName("Cp1251");
    //private static final HttpTaskManager httpTaskManager = (HttpTaskManager) Managers.getDefault();
    // TODO: 22.03.2023 get key
    //private static final HttpTaskManager httpTaskManager = HttpTaskManager.load("");
    private HttpTaskManager httpTaskManager;
    //  private final String key;

    /*  public HttpTaskServer(String key) {
          //this.key = key;
          httpTaskManager = HttpTaskManager.load(key);
      }*/
    public HttpTaskServer(HttpTaskManager httpTaskManager) {
       // httpTaskManager = (HttpTaskManager) Managers.getDefault();
       /*String key = httpTaskManager.getKvTaskClient().getKey();
       httpTaskManager = httpTaskManager.load(key);*/
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        /*KVTaskClient kvTaskClient = new KVTaskClient(URI.create("http://localhost:8078/register/"));
        String key = kvTaskClient.getKey();*/
//        httpTaskManager = HttpTaskManager.load(key);

        String method = exchange.getRequestMethod();

        URI requestURI = exchange.getRequestURI();
        // Из экземпляра URI получить requestPath.
        String requestPath = requestURI.getPath();

        // получите информацию об эндпоинте, к которому был запрос
        Endpoint endpoint = getEndpoint(requestPath, method);

        switch (endpoint) {
            case GET_HISTORY -> {
                writeResponse(exchange, "Получен запрос на получение истории задач", 200);
                List<AbstractTask> historyTask = httpTaskManager.getHistory();
                System.out.println("Test exist httpTaskManager");
                //sendRequest("history");
            }
            case GET_ALL_TASKS -> {
                writeResponse(exchange, "Получен запрос на получение списка всех задач", 200);
                Map<Integer, AbstractTask> standardTasks = httpTaskManager.getStandardTasks();
                Map<Integer, AbstractTask> epics = httpTaskManager.getEpicTasks();
                System.out.println("Test get standard tasks" + standardTasks + " " + epics);
            }
            case GET_STANDARD_TASKS -> {
                writeResponse(exchange, "Получен запрос на получение обычных задач", 200);
            }
            case GET_EPIC_TASKS -> {
                writeResponse(exchange, "Получен запрос на получение эпиков", 200);
            }
            case GET_PRIORITIZED_TASKS -> {
                writeResponse(exchange, "Получен запрос на получение упорядоченного по времени списка задач", 200);
                TreeSet<AbstractTask> apiTasks = httpTaskManager.getPrioritizedTasks();
                System.out.println("apiTasks" + apiTasks);
            }
            case GET_FIND_TASK_BY_ID -> {
                writeResponse(exchange, "Получен запрос на получение задачи по id", 200);
            }
            case POST_ADD_EPIC -> {
                writeResponse(exchange, "Получен запрос на добавление эпика", 200);
            }
            case POST_ADD_TASK -> {
                writeResponse(exchange, "Получен запрос на добавления задачи", 200);
            }
            case POST_CREATE_TASK -> {
                writeResponse(exchange, "Получен запрос на создание задачи", 200);
            }
            case POST_CREATE_SUBTASK -> {
                writeResponse(exchange, "Получен запрос на создание подзадачи", 200);
            }
            case POST_CREATE_EPIC -> {
                writeResponse(exchange, "Получен запрос на создание эпика", 200);
            }
            case DELETE_DELETE_TASK_BY_ID -> {
                writeResponse(exchange, "Получен запрос на удаление задачи по id", 200);
            }
            case DELETE_DELETE_ALL_TASKS -> {
                writeResponse(exchange, "Получен запрос на удаление всех задач", 200);
            }
            default -> writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
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
                    case "createtask" -> {
                        return POST_CREATE_TASK;
                    }
                    case "createsubtask" -> {
                        return POST_CREATE_SUBTASK;
                    }
                    case "createepic" -> {
                        return POST_CREATE_EPIC;
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
                os.write(responseString.getBytes(CP1251_CHARSET));
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
