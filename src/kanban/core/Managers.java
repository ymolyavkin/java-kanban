package kanban.core;

public class Managers {

    static InMemoryTaskManager inMemoryTaskManager = InMemoryTaskManager.getInstance();
    static InMemoryHistoryManager inMemoryHistoryManager = InMemoryHistoryManager.getInstance();

    public static TaskManager getDefault() {
        return inMemoryTaskManager;
    }
    public static HistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }
}
