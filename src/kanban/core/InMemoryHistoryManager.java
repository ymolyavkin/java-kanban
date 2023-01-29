package kanban.core;

import kanban.model.AbstractTask;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private List<AbstractTask> historyBrowsingTask;
    // TODO: Maybe  doublyLinkedList is useless
    // the doublyLinkedList may not be necessary
    private List<AbstractTask> doublyLinkedList;
    private Map<Integer, Node> historyViewMap;
    private final int CAPACITYHISTORY = 10;

    public InMemoryHistoryManager() {

        historyBrowsingTask = new ArrayList<>();
        // TODO: Поменять реализацию (возможно, оставить только один список)
        // To change implementation (maybe keep only single list)
        // Change the implementation (maybe keep only one list)
        doublyLinkedList = new ArrayList<>();
        historyViewMap = new HashMap<>();
    }

    /**
     * Returns {@code true} if this map should remove its eldest entry.
     * This method is invoked by {@code put} and {@code putAll} after
     * inserting a new entry into the map.  It provides the implementor
     * with the opportunity to remove the eldest entry each time a new one
     * is added.  This is useful if the map represents a cache: it allows
     * the map to reduce memory consumption by deleting stale entries.
     * @param eldest
     */
    protected boolean removeEldestEntry(Map.Entry<Integer, Node> eldest) {
        return historyViewMap.size() > CAPACITYHISTORY;
    }

    @Override
    public void add(AbstractTask task) {
        // TODO:
        //  with help HashMap and the method removeNode this method fast deleting a task from list,
        //  if she is exist here, then insert him into end doubly linked list
        /**
         * With the HashMap and the removeNode delete method, this method will quickly delete a task
         * from the list if it is there, and then insert it at the end of the double-linked list.
         */
        if (historyViewMap.containsKey(task.getId())) {
            this.remove(task.getId());
        }
        historyViewMap.put(task.getId(), new Node<>(task));

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
        historyViewMap.remove(id);
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
