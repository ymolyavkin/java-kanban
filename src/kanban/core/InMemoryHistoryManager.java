package kanban.core;

import kanban.model.AbstractTask;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static InMemoryHistoryManager instance;
    private List<AbstractTask> historyBrowsingTask;
    private final int CAPACITYHISTORY = 10;

    public InMemoryHistoryManager() {
        historyBrowsingTask = new LinkedList<>();
    }

    public static InMemoryHistoryManager getInstance() {
        if (instance == null) {
            instance = new InMemoryHistoryManager();
        }
        return instance;
    }


    @Override
    public void add(AbstractTask task) {
        historyBrowsingTask.add(task);
        while (historyBrowsingTask.size() > CAPACITYHISTORY) {
            historyBrowsingTask.remove(0);
        }
    }

    /**
     * @return tasks list
     */
    @Override
    public List<AbstractTask> getHistory() {
        return historyBrowsingTask;
    }

}
