package kanban.core;

import java.net.URI;
import java.nio.file.Path;

public class Managers {
    public static InMemoryTaskManager inMemoryTaskManager = InMemoryTaskManager.getInstance();
    private static InMemoryHistoryManager historyManager;
    private static FileBackedTasksManager fileBackedTasksManager;
    private static HttpTaskManager httpTaskManager;
    private static String url = "http://localhost:8078/register";


    public static TaskManager getDefault() {
        httpTaskManager = new HttpTaskManager(URI.create(url));

        return httpTaskManager;
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        historyManager = new InMemoryHistoryManager();

        return historyManager;
    }

    public static FileBackedTasksManager getFileBackedTasksManager(Path path) {
        fileBackedTasksManager
                = new FileBackedTasksManager(path);
        return fileBackedTasksManager;
    }
}
