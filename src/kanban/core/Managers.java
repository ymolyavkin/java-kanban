package kanban.core;

import java.nio.file.Path;

public class Managers {
    public static InMemoryTaskManager inMemoryTaskManager = InMemoryTaskManager.getInstance();
    private static InMemoryHistoryManager historyManager;
   // private static InFileHistoryManager inFileHistoryManager;
    private static FileBackedTasksManager fileBackedTasksManager;


    public static TaskManager getDefault() {

        return inMemoryTaskManager;
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
