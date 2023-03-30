package kanban.taskapi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static kanban.visual.Main.KV_PORT;

public class KVServer {
    //public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", KV_PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
        server.createContext("/clear", this::clear);
    }

    private void clear(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/clear");
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("DELETE".equals(h.getRequestMethod())) {
                //String key = h.getRequestURI().getPath().substring("/clear/".length());
                //System.out.println("Delete key: " + key);

                /*if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.equals("{}") || value.equals("[]") || value.isBlank() || value == null) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }*/
                data.clear();

                System.out.println("Все данные из хранилища удалены");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/clear ждёт DELETE-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
                String response = "Все данные удалены из хранилища";
                h.sendResponseHeaders(200, 0);
                try (OutputStream os = h.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } finally {
            h.close();
        }
        //**************************
        /*data.clear();
        String response = "Все данные удалены из хранилища";
        h.sendResponseHeaders(200, 0);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response.getBytes());
        }*/
    }

    private void load(HttpExchange h) throws IOException {
        // TODO Добавьте получение значения по ключу
        try {
            System.out.println("\n/load");
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String[] pathParts = h.getRequestURI().getPath().split("/");


                String response;
                if (data.isEmpty()) {
                    response = "response From KVserver: Хранилище пусто";
                } else {
                    System.out.println("Ключ: " + pathParts[2]);
                    response = data.get(pathParts[2]);
                    System.out.println("From KVserver: " + response);
                }
                h.sendResponseHeaders(200, 0);
                try (OutputStream os = h.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                System.out.println("/load ждёт GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
        //*****************************************
        /*String[] pathParts = h.getRequestURI().getPath().split("/");


        String response;
        if (data.isEmpty()) {
            response = "response From KVserver: Хранилище пусто";
        } else {
            response = data.get(pathParts[2]);
            System.out.println("From KVserver: " + response);
        }
        h.sendResponseHeaders(200, 0);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response.getBytes());
        }*/

       /* Headers rmap = h.getRequestHeaders();
        System.out.println("rmap = " + rmap);*/
        /*InputStream inputStream = h.getRequestBody();

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));*/
        // System.out.println("From KVserer br: ");
        //   System.out.println(br.lines().collect(Collectors.joining(System.lineSeparator())));

    }

    private void save(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/save");
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.equals("{}") || value.equals("[]") || value.isBlank() || value == null) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("value = " + value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/register");
            System.out.println("h.getRequestMethod() = " + h.getRequestMethod());
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + KV_PORT);
        System.out.println("Открой в браузере http://localhost:" + KV_PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }
    public void stop() {
        System.out.println("Останавливаем KVServer");

        server.stop(1);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    /*private void createHttpClient(String resources) {
        // используем код состояния как часть URL-адреса
        URI uri = URI.create("http://localhost:8080/tasks/" + resources);
        KVTaskClient kvTaskClient = new KVTaskClient(uri);
        String response = kvTaskClient.sendRequest(uri);
        System.out.println("response = " + response);
    }*/
}
