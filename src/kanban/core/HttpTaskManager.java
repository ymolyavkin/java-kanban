package kanban.core;

import kanban.tasksAPI.KVTaskClient;

import java.net.URI;

public class HttpTaskManager extends FileBackedTasksManager {
    public HttpTaskManager(URI url) {
        KVTaskClient kvTaskClient = new KVTaskClient(url);
    }
}
