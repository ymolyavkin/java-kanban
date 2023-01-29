package kanban.core;

import kanban.model.AbstractTask;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private List<AbstractTask> historyBrowsingTask;
    // TODO: Maybe  doublyLinkedList is useless
    // the doublyLinkedList may not be necessary
    private List<AbstractTask> doublyLinkedList;
    private Map<Integer, Node> taskViewMap;
    private final int CAPACITYHISTORY = 10;

    public InMemoryHistoryManager() {

        historyBrowsingTask = new ArrayList<>();
        // TODO: Поменять реализацию (возможно, оставить только один список)
        // To change implementation (maybe keep only single list)
        // Change the implementation (maybe keep only one list)
        doublyLinkedList = new ArrayList<>();
        taskViewMap = new HashMap<>();
    }

    @Override
    public void add(AbstractTask task) {
        historyBrowsingTask.add(task);
        while (historyBrowsingTask.size() > CAPACITYHISTORY) {
            historyBrowsingTask.remove(0);
        }
    }

    /**
     * @param id
     */
    @Override
    public void remove(int id) {
        // TODO: удалить задачу из истории просмотра если добавляется задача с таким же id
        // delete a task from browsing history if added a task have similar id
        // remove the task from the viewing history if a task with the same id is added
    }

    public void removeNode(Node node) {
        // TODO: удалить узел двусвязного списка (отображения)
        // delete the node of doubly linked list (map)
        // delete a node in a doubly linked list (map)
    }

    /**
     * @return tasks list
     */
    @Override
    public List<AbstractTask> getHistory() {
        // Implementation this method must overput tasks from doubly linked list to ArrayList
        //The implementation of this method should transfer the tasks from the linked list to the ArrayList
        return historyBrowsingTask;
    }

    public void linkLast(AbstractTask task) {
        // TODO: добавить задачу в конец двусвязного списка
        // add a task into end two-linked list
        // add the task to the end of the doubly linked list

    }

    public List<AbstractTask> getTasks() {
        // TODO: собирает все задачи из двусвязного списка в ArrayList
        // accumulate all tasks from two-linked list into ArrayList
        // collects all tasks from the double-linked list into an ArrayList
        return doublyLinkedList;
    }
}
