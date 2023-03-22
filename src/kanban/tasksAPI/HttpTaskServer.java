package kanban.tasksAPI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static kanban.tasksAPI.Endpoint.*;

public class HttpTaskServer implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Charset CP1251_CHARSET = Charset.forName("Cp1251");

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        URI requestURI = exchange.getRequestURI();
        // Из экземпляра URI получить requestPath.
        String requestPath = requestURI.getPath();

        // получите информацию об эндпоинте, к которому был запрос
        Endpoint endpoint = getEndpoint(requestPath, method);

        switch (endpoint) {
            case GET_HISTORY -> {
                writeResponse(exchange, "Получен запрос на получение истории задач", 200);
            }
            case GET_ALL_TASKS -> {
                writeResponse(exchange, "Получен запрос на получение списка всех задач", 200);
            }
            case GET_STANDARD_TASKS -> {
                writeResponse(exchange, "Получен запрос на получение обычных задач", 200);
            }
            case GET_EPIC_TASKS -> {
                writeResponse(exchange, "Получен запрос на получение эпиков", 200);
            }
            case GET_PRIORITIZED_TASKS -> {
                writeResponse(exchange, "Получен запрос на получение упорядоченного по времени списка задач", 200);
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
            case POST_DELETE_TASK_BY_ID -> {
                writeResponse(exchange, "Получен запрос на удаление задачи по id", 200);
            }
            case POST_DELETE_ALL_TASKS -> {
                writeResponse(exchange, "Получен запрос на удаление всех задач", 200);
            }
            default -> writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        // реализуйте этот метод

        String[] splitStrings = requestPath.split("/");
        String lastPart = splitStrings[splitStrings.length - 1];

        if (requestMethod.equals("GET")) {
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
        } else if (requestMethod.equals("POST")) {
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
                case "deletetaskbyid" -> {
                    return POST_DELETE_TASK_BY_ID;
                }
                case "deletealltasks" -> {
                    return POST_DELETE_ALL_TASKS;
                }
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {

        exchange.sendResponseHeaders(responseCode, 0);

        if (!responseString.isEmpty()) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseString.getBytes(CP1251_CHARSET));
            }
        }
    }
}
