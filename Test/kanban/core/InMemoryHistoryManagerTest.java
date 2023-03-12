package kanban.core;

import kanban.model.EpicTask;
import kanban.model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private final HistoryManager historyManager = new InMemoryHistoryManager();
    private static Task firstTask;
    private static Task secondTask;
    private static Task thirdTask;
    private static EpicTask epic;

    @BeforeAll
    static void init() {
        final String title = "Title";
        final String description = "Description";
        final int epicId = 0;
        final int firstId = 1;
        final int secondId = 2;
        final int thirdId = 3;

        firstTask = new Task(title, description, firstId);
        secondTask = new Task(title, description, secondId);
        thirdTask = new Task(title, description, thirdId);
        epic = new EpicTask(title, description, epicId);
    }

    @Test
    void testAddEmptyHistory() {
        if (historyManager.getHistory() == null) {

            historyManager.add(firstTask);
            historyManager.add(secondTask);
            historyManager.add(thirdTask);
            historyManager.add(epic);

            var history = historyManager.getHistory();
            assertEquals(4, history.size());
        }
    }

    @Test
    void testAddDoubleTask() {
        var history = historyManager.getHistory();
        final int size = history.size();

        historyManager.add(firstTask);
        historyManager.add(secondTask);
        historyManager.add(firstTask);
        historyManager.add(secondTask);

        history = historyManager.getHistory();
        assertEquals(size + 2, history.size());
    }

    @Test
    void testRemoveMiddle() {
        var history = historyManager.getHistory();
        final int size = history.size();

        historyManager.add(firstTask);
        historyManager.add(secondTask);
        historyManager.add(thirdTask);

        historyManager.remove(secondTask.getId());

        history = historyManager.getHistory();
        assertEquals(size + 2, history.size());

        history = historyManager.getHistory();

        assertTrue(history.contains(firstTask));
        assertTrue(history.contains(thirdTask));
        assertFalse(history.contains(secondTask));
    }

    @Test
    void testRemoveFirst() {
        var history = historyManager.getHistory();
        final int size = history.size();

        historyManager.add(firstTask);
        historyManager.add(secondTask);
        historyManager.add(thirdTask);

        historyManager.remove(firstTask.getId());

        history = historyManager.getHistory();
        assertEquals(size + 2, history.size());

        assertTrue(history.contains(secondTask));
        assertTrue(history.contains(thirdTask));
        assertFalse(history.contains(firstTask));
    }

    @Test
    void testRemoveLast() {
        var history = historyManager.getHistory();
        final int size = history.size();

        historyManager.add(firstTask);
        historyManager.add(secondTask);
        historyManager.add(thirdTask);

        historyManager.remove(thirdTask.getId());

        history = historyManager.getHistory();
        assertEquals(size + 2, history.size());

        assertTrue(history.contains(firstTask));
        assertTrue(history.contains(secondTask));
        assertFalse(history.contains(thirdTask));
    }

}