package kanban.core;

import kanban.model.AbstractTask;

import java.util.List;

public class Managers {
    static InMemoryTaskManager inMemoryTaskManager = InMemoryTaskManager.getInstance();
    static InMemoryHistoryManager inMemoryHistoryManager = InMemoryHistoryManager.getInstance();

    public static TaskManager getDefault() {
        return inMemoryTaskManager;
    }

    public static List<AbstractTask> getDefaultHistory() {
        return inMemoryHistoryManager.getHistory();
    }
}
