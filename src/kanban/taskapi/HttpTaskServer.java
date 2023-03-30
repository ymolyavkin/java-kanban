package kanban.taskapi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import kanban.core.HttpTaskManager;
import kanban.model.AbstractTask;
import kanban.model.EpicTask;
import kanban.model.Subtask;
import kanban.model.Task;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static kanban.taskapi.Endpoint.*;

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
        String response = "";

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
                List<AbstractTask> history = httpTaskManager.getHistory();
                String jsonHistory = httpTaskManager.objectToJson(history);
                response += "ArrayList<AbstractTask>;" + jsonHistory;

                System.out.println("GET_HISTORY -> " + response);
                writeResponse(httpExchange, response, 200);
            }
            case GET_ALL_TASKS -> {
                Map<Integer, AbstractTask> standardTasks = httpTaskManager.getStandardTasks();
                Map<Integer, AbstractTask> epics = httpTaskManager.getEpicTasks();

                String jsonTasks = httpTaskManager.objectToJson(standardTasks);
                response += "Map<Integer, Task>;" + jsonTasks;

                String jsonEpics = httpTaskManager.objectToJson(epics);
                response += ";Map<Integer, EpicTask>;" + jsonEpics;

                System.out.println("GET_ALL_TASKS -> " + response);
                writeResponse(httpExchange, response, 200);
            }
            case GET_STANDARD_TASKS -> {
                Map<Integer, AbstractTask> standardTasks = httpTaskManager.getStandardTasks();

                String jsonTasks = httpTaskManager.objectToJson(standardTasks);
                response += "Map<Integer, Task>;" + jsonTasks;

                System.out.println("GET_STANDARD_TASKS -> " + response);
                writeResponse(httpExchange, response, 200);
            }
            case GET_EPIC_TASKS -> {
                Map<Integer, AbstractTask> epics = httpTaskManager.getEpicTasks();

                String jsonEpics = httpTaskManager.objectToJson(epics);
                response += "Map<Integer, EpicTask>;" + jsonEpics;

                System.out.println("GET_EPIC_TASKS -> " + response);
                writeResponse(httpExchange, response, 200);
            }
            case GET_PRIORITIZED_TASKS -> {
                TreeSet<AbstractTask> prioritized = httpTaskManager.getPrioritizedTasks();

                String jsonPrioritized = httpTaskManager.objectToJson(prioritized);
                response += "TreeSet<AbstractTask>;" + jsonPrioritized;

                System.out.println("GET_PRIORITIZED_TASKS -> " + response);
                writeResponse(httpExchange, response, 200);
            }
            case GET_FIND_TASK_BY_ID -> {
                String rawQuery = httpExchange.getRequestURI().getRawQuery();
                // System.lineSeparator()
                //System.out.println(rawQuery);
                int id = Integer.valueOf(getIdTask(rawQuery));
                AbstractTask foundTask = httpTaskManager.findTaskByIdOrNull(id, true);
                if (foundTask != null) {
                    String jsonTask = httpTaskManager.abstractTaskToJson(foundTask);
                    if (foundTask instanceof Task) {
                        response += "task;" + jsonTask;
                    } else if (foundTask instanceof EpicTask) {
                        response += "epic;" + jsonTask;
                    } else if (foundTask instanceof Subtask) {
                        response += "subtask;" + jsonTask;
                    }
                } else {
                    response = "Задача с id " + id + "не найдена";
                }
                System.out.println("GET_FIND_TASK_BY_ID -> " + response);
                writeResponse(httpExchange, response, 200);
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
            case POST_UPDATE_TASK -> {
                System.out.println("Ендпоинт POST_UPDATE_TASK");
                // извлекаем тело запроса
                InputStream inputStream = httpExchange.getRequestBody();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String body = bufferedReader.readLine();
                System.out.println("body" + body);

                Task task = httpTaskManager.restoreTaskFromJson(body);
                httpTaskManager.addTask(task);

                writeResponse(httpExchange, "Получен запрос на обновление задачи", 200);
            }
            case POST_UPDATE_EPIC -> {
                System.out.println("Ендпоинт POST_UPDATE_EPIC");
                // извлекаем тело запроса
                InputStream inputStream = httpExchange.getRequestBody();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String body = bufferedReader.readLine();
                System.out.println("body" + body);

                EpicTask epic = httpTaskManager.restoreEpicFromJson(body);
                httpTaskManager.addEpic(epic);

                writeResponse(httpExchange, "Получен запрос на обновление эпика", 200);
            }
            case DELETE_DELETE_TASK_BY_ID -> {

                String rawQuery = httpExchange.getRequestURI().getRawQuery();

                int id = Integer.valueOf(getIdTask(rawQuery));
                if (httpTaskManager.deleteTaskById(id)) {
                    response = "Удалена задача с id " + String.valueOf(id);
                    System.out.println("Удалена задача с id " + id);
                   // httpExchange.sendResponseHeaders(200, 0);
                } else {
                    response = "Получен некорректный id: " + String.valueOf(id);
                    System.out.println(response);
                   // httpExchange.sendResponseHeaders(405, 0);
                }
                writeResponse(httpExchange, response, 200);
            }
            case DELETE_DELETE_ALL_TASKS -> {
                /*System.out.println("Ендпоинт POST_ADD_TASK");
                // извлекаем тело запроса
                InputStream inputStream = httpExchange.getRequestBody();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String body = bufferedReader.readLine();
                System.out.println("body" + body);

                Task task = httpTaskManager.restoreTaskFromJson(body);
                httpTaskManager.addTask(task);
                System.out.println("Ендпоинт POST_ADD_TASK");*/
                //String answer;
                if (httpTaskManager.deleteAllTasks()) {
                    writeResponse(httpExchange, "Все задачи удалены", 200);
                } else {
                    writeResponse(httpExchange, "Задачи удалить не удалось", 405);
                }
            }
            default -> writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
        }
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    /*private int handleEndpointDeleteTaskBuId(HttpExchange httpExchange) {
     *//* String key = httpExchange.getRequestURI().getPath().substring("/delete/".length());

        String path = httpExchange.getRequestURI().getPath();

        String rawPath = httpExchange.getRequestURI().getRawPath();*//*

        URI deleteUri = httpExchange.getRequestURI();
        String rawQuery = deleteUri.getRawQuery();

        return Integer.valueOf(getIdTask(rawQuery));
    }*/

    private String getIdTask(String rawPath) {
        String regEx = "^.*id=([\\d]+).*$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(rawPath);
        String id = "";
        if (matcher.find()) {
            id = matcher.group(1);
        }
        return id;
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
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
                    case "task" -> {
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
                    case "task" -> {
                        return POST_UPDATE_TASK;
                    }
                    case "epic" -> {
                        return POST_UPDATE_EPIC;
                    }
                }
            }
            case "DELETE" -> {
                switch (lastPart) {
                    case "task" -> {
                        return DELETE_DELETE_TASK_BY_ID;
                    }
                    case "alltasks" -> {
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
}
