package kanban.core;

import kanban.model.AbstractTask;

public class InMemoryHistoryManager implements HistoryManager {
    private int capacity;
    private QueueTask queueTask = new QueueTask(capacity);

    /**
     * @param task
     */
    @Override
    public void add(AbstractTask task) {
        queueTask.addTaskToQueue(task);
    }

    /**
     * @return tasks list
     */
    @Override
    public QueueTask getHistory() {
        return queueTask.getQueueTask();
    }
}
