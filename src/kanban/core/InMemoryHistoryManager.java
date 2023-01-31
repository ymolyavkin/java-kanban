package kanban.core;

import kanban.model.AbstractTask;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node<AbstractTask>> historyViewMap;
    private Node<AbstractTask> headNode;
    private Node<AbstractTask> tailNode;
    private final int CAPACITYHISTORY = 10;

    public InMemoryHistoryManager() {

        historyViewMap = new HashMap<>();
    }

    private void removeEldestEntryIfOverSize() {
        while (historyViewMap.size() > CAPACITYHISTORY) {
            removeNode(headNode);
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

        Node<AbstractTask> prevNode = node.getPrev();
        Node<AbstractTask> nextNode = node.getNext();

        historyViewMap.remove(node.getData().getId());

        if (prevNode == null) {
            headNode = nextNode;
        } else {
            prevNode.setNext(nextNode);
            historyViewMap.put(prevNode.getData().getId(), prevNode);
        }
        if (nextNode == null) {
            tailNode = prevNode;
        } else {
            nextNode.setPrev(prevNode);
            historyViewMap.put(nextNode.getData().getId(), nextNode);
        }
    }

    public void removeAllNodes() {
        historyViewMap.clear();
    }

    private void addNode(Node<AbstractTask> node) {
        AbstractTask task = node.getData();
        int id = task.getId();

        if (historyViewMap.isEmpty()) {
            historyViewMap.put(id, node);

            headNode = node;
            tailNode = node;

        } else {
            tailNode.setNext(node);
            node.setPrev(tailNode);

            historyViewMap.put(tailNode.getData().getId(), tailNode);
            historyViewMap.put(id, node);

            tailNode = node;

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
        if (!historyViewMap.isEmpty()) {
            int historySize = historyViewMap.size();
            int currentId = headNode.getData().getId();

            for (int i = 0; i < historySize; i++) {
                Node<AbstractTask> currentHead = historyViewMap.get(currentId);

                AbstractTask currentTask = currentHead.getData();

                historyList.add(currentTask);
                if (currentHead.getNext() == null) {
                    break;
                }
                Node<AbstractTask> nextNode = currentHead.getNext();
                currentId = nextNode.getData().getId();
            }
        }
        return historyList;
    }
}
