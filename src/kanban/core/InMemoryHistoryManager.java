package kanban.core;

import kanban.model.AbstractTask;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node<AbstractTask>> historyViewMap;
    private int idHead;
    private int idTail;
    private final int CAPACITYHISTORY = 10;

    public InMemoryHistoryManager() {

        historyViewMap = new HashMap<>();
    }

    private void removeEldestEntryIfOverSize() {
        while (historyViewMap.size() > CAPACITYHISTORY) {
            removeHeadNode();
        }
    }

    @Override
    public void add(AbstractTask task) {
        if (historyViewMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (historyViewMap.size() == 1) {
            if (historyViewMap.containsKey(id)) {
                historyViewMap.remove(id);
            }

        } else {
            if (historyViewMap.containsKey(id)) {
                Node<AbstractTask> removalNode = historyViewMap.get(id);
                removeNode(removalNode);
            }
        }
    }

    private void removeNode(Node<AbstractTask> node) {
        if (node.prev == null) {
            removeHeadNode();
        } else if (node.next == null) {
            removeLastNode();
        } else {
            Node<AbstractTask> prevNode = node.prev;
            Node<AbstractTask> nextNode = node.next;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
            historyViewMap.remove(node.data.getId());
            historyViewMap.put(prevNode.data.getId(), prevNode);
            historyViewMap.put(nextNode.data.getId(), nextNode);
        }
    }

    private void removeHeadNode() {
        Node<AbstractTask> headNode = historyViewMap.get(idHead);
        Node<AbstractTask> nextNode = headNode.next;

        historyViewMap.remove(idHead);

        nextNode.prev = null;
        AbstractTask firstTask = nextNode.data;
        idHead = firstTask.getId();

        historyViewMap.put(idHead, nextNode);
    }

    private void removeLastNode() {
        Node<AbstractTask> lastNode = historyViewMap.get(idTail);
        Node<AbstractTask> prevLastNode = lastNode.prev;
        prevLastNode.next = null;

        historyViewMap.remove(idTail);
        idTail = prevLastNode.data.getId();

        historyViewMap.put(idTail, prevLastNode);
    }

    public void removeAllNodes() {
        historyViewMap.clear();
    }

    private void addNode(Node<AbstractTask> node) {
        AbstractTask task = node.data;
        int id = task.getId();

        if (historyViewMap.isEmpty()) {
            historyViewMap.put(id, node);

            idHead = id;
            idTail = id;

        } else {
            Node<AbstractTask> lastNode = historyViewMap.get(idTail);
            lastNode.next = node;
            node.prev = lastNode;

            historyViewMap.put(idTail, lastNode);
            historyViewMap.put(id, node);

            idTail = id;

            removeEldestEntryIfOverSize();
        }
    }

    @Override
    public List<AbstractTask> getHistory() {
        return getTasks();
    }

    private void linkLast(AbstractTask task) {
        Node<AbstractTask> taskNode = new Node<>(task);

        addNode(taskNode);
    }

    private List<AbstractTask> getTasks() {
        List<AbstractTask> historyList = new ArrayList<>();
        int historySize = historyViewMap.size();
        int currentId = idHead;

        for (int i = 0; i < historySize; i++) {
            Node<AbstractTask> currentHead = historyViewMap.get(currentId);

            AbstractTask currentTask = currentHead.data;

            historyList.add(currentTask);
            if (currentHead.next == null) {
                break;
            }
            Node<AbstractTask> nextNode = currentHead.next;
            currentId = nextNode.data.getId();
        }
        return historyList;
    }
}
