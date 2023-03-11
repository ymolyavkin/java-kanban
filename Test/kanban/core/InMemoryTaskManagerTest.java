package kanban.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
//public class InMemoryTaskManagerTest<T extends TaskManager> extends TaskManagerTest {
    @BeforeEach
    void setUp() {
       taskManager = new InMemoryTaskManager();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getInMemoryHistoryManager() {
    }

    @Test
    void getInstance() {
    }

    @Test
    void getHistory() {
    }

    @Test
    void testAddTask() {
    }

    @Test
    void addEpic() {
    }

    @Test
    void testCreateStandardTask() {
        super.testCreateStandardTask();
    }

    @Test
    void restoreStandardTaskWithId() {
        super.restoreStandardTaskWithId();
    }

    @Test
    void updateStandardTask() {
        super.updateStandardTask();
    }

    @Test
    void updateEpic() {
    }

    @Test
    void updateSubtask() {
    }

    @Test
    void createSubtask() {
    }

    @Test
    void createSubtaskWithId() {
    }

    @Test
    void createEpic() {
    }

    @Test
    void createEpicWithId() {
    }

    @Test
    void addSubtaskToEpic() {
    }

    @Test
    void getStandardTasks() {
    }

    @Test
    void getPrioritizedTasks() {
    }

    @Test
    void getEpicTasks() {
    }

    @Test
    void addTaskIntoHistory() {
    }

    @Test
    void findSubtaskByIdOrNull() {
    }

    @Test
    void findTaskByIdOrNull() {
    }

    @Test
    void deleteTaskById() {
    }

    @Test
    void deleteAllTasks() {
    }

    @Test
    void testGetInMemoryHistoryManager() {
    }

    @Test
    void testGetInstance() {
    }

    @Test
    void testGetHistory() {
    }

    @Test
    void testAddTask1() {
    }

    @Test
    void testAddEpic() {
    }

    @Test
    void testCreateStandardTask1() {
    }

    @Test
    void testCreateStandardTaskWithId() {
    }

    @Test
    void testUpdateStandardTask() {
    }

    @Test
    void testUpdateEpic() {
    }

    @Test
    void testUpdateSubtask() {
    }

    @Test
    void testCreateSubtask() {
    }

    @Test
    void testCreateSubtaskWithId() {
    }

    @Test
    void testCreateEpic() {
    }

    @Test
    void testCreateEpicWithId() {
    }

    @Test
    void testAddSubtaskToEpic() {
    }

    @Test
    void testGetStandardTasks() {
    }

    @Test
    void testGetPrioritizedTasks() {
    }

    @Test
    void testGetEpicTasks() {
    }

    @Test
    void testAddTaskIntoHistory() {
    }

    @Test
    void testFindSubtaskByIdOrNull() {
    }

    @Test
    void testFindTaskByIdOrNull() {
    }

    @Test
    void testDeleteTaskById() {
    }

    @Test
    void testDeleteAllTasks() {
    }
}
