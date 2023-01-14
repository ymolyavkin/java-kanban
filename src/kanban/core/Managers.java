package kanban.core;

public class Managers {

    static InMemoryTaskManager inMemoryTaskManager = InMemoryTaskManager.getInstance();

    public static TaskManager getDefault() {
        return inMemoryTaskManager;
    }
}
