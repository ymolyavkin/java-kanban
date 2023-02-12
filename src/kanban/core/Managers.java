package kanban.core;

public class Managers {
    public static InMemoryTaskManager inMemoryTaskManager = InMemoryTaskManager.getInstance();
    private static InMemoryHistoryManager historyManager;
    private static InFileHistoryManager inFileHistoryManager;

    public static TaskManager getDefault() {

        return inMemoryTaskManager;
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        historyManager = new InMemoryHistoryManager();

        return historyManager;
    }
    public static InFileHistoryManager getFromFileHistory() {
        inFileHistoryManager = new InFileHistoryManager();

        return inFileHistoryManager;
    }

}
