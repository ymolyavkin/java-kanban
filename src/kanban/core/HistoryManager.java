package kanban.core;

import kanban.model.AbstractTask;

public interface HistoryManager {
    void add(AbstractTask task);

    QueueTask getHistory();
}
