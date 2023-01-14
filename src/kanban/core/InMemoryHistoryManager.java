package kanban.core;

import kanban.model.AbstractTask;

public class InMemoryHistoryManager implements HistoryManager {
    private static InMemoryHistoryManager instance;
    private QueueTask queueTask;
    private final int CAPACITYHISTORY = 10;
    private int key;
    public InMemoryHistoryManager() {
        queueTask = new QueueTask(CAPACITYHISTORY);
        key = -1;
    }

    public static InMemoryHistoryManager getInstance() {
        if (instance == null) {
            instance = new InMemoryHistoryManager();
        }
        return instance;
    }
    private int getKey() {
        if (key >= Integer.MAX_VALUE - 1) {
            key = 0;
        } else key++;
        return key;
    }
    /**
     * @param task
     */
    @Override
    public void add(AbstractTask task) {
        queueTask.put(getKey(), task);
    }

    /**
     * @return tasks list
     */
    @Override
    public QueueTask getHistory() {
        return queueTask;
    }

}
