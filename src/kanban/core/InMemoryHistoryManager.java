package kanban.core;

import kanban.model.AbstractTask;

public class InMemoryHistoryManager implements HistoryManager {
    private int capacity;
    private int key;
    private QueueTask queueTask;
    private final int CAPACITYHISTORY = 10;

    public InMemoryHistoryManager() {
        queueTask = new QueueTask(CAPACITYHISTORY);
        key=0;
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
    private int getKey() {
        if (key >= capacity - 1) {
            key = 0;
        } else key++;
        return key;
    }
}
