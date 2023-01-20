package kanban.core;

import kanban.model.AbstractTask;

import java.util.List;

public class Managers {
    public static InMemoryTaskManager inMemoryTaskManager = InMemoryTaskManager.getInstance();
    public static InMemoryHistoryManager inMemoryHistoryManager = InMemoryHistoryManager.getInstance();

    public static TaskManager getDefault() {

        return inMemoryTaskManager;
    }

    /*public static InMemoryHistoryManager getDefaultHistory() {

        return inMemoryHistoryManager;
    }*/
    public static List<AbstractTask> getDefaultHistory() {

        return inMemoryHistoryManager.getHistory();
    }

}
