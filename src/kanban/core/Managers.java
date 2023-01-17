package kanban.core;

import kanban.model.AbstractTask;

import java.util.List;

public class Managers {
    static InMemoryTaskManager inMemoryTaskManager = InMemoryTaskManager.getInstance();

    public static TaskManager getDefault() {

        return inMemoryTaskManager;
    }

    public static List<AbstractTask> getDefaultHistory() {
        return InMemoryTaskManager.historyManager.getHistory();
    }


}
