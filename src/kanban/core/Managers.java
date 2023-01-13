package kanban.core;

public class Managers {
    public TaskManager getDefault() {
        return null;
    }

    public static HistoryManager getDefaultHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        return inMemoryHistoryManager;
    }
}
