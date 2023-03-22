package kanban.tasksAPI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
            case GET_POSTS: {
                writeResponse(exchange, "Получен запрос на получение постов", 200);
                break;
            }
            case GET_COMMENTS: {
                writeResponse(exchange, "Получен запрос на получение комментариев", 200);
                break;
            }
            case POST_COMMENT: {
                writeResponse(exchange, "Получен запрос на добавление комментария", 200);
                break;
            }
            default:
                writeResponse(exchange, "Answer: Такого эндпоинта не существует", 404);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        // реализуйте этот метод

        String[] splitStrings = requestPath.split("/");
        String lastPart = splitStrings[splitStrings.length - 1];


        switch (requestMethod) {
            case "POST":
                if (lastPart.equals("comments")) {
                    return Endpoint.POST_COMMENT;
                }
                break;
            case "GET":
                //response = "Вы использовали метод GET!";
                if (lastPart.equals("comments")) {
                    return Endpoint.GET_COMMENTS;
                } else if (lastPart.equals("posts")) {
                    return Endpoint.GET_POSTS;
                }
                break;
            default:
                // "Вы использовали какой-то другой метод!";
                return Endpoint.UNKNOWN;
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
