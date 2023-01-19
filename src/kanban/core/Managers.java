package kanban.core;

public class Managers {
    public static InMemoryTaskManager inMemoryTaskManager = InMemoryTaskManager.getInstance();
    public static InMemoryHistoryManager inMemoryHistoryManager = InMemoryHistoryManager.getInstance();

    public static TaskManager getDefault() {

        return inMemoryTaskManager;
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }


}
