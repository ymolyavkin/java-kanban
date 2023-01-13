package kanban.core;

import kanban.model.AbstractTask;

import java.util.List;

public interface HistoryManager {
    void add(AbstractTask task);
    QueueTask getHistory();
}
