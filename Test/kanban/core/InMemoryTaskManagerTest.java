package kanban.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        taskManager.deleteAllTasks();
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
        super.updateEpic();
    }

    @Test
    void updateSubtask() {
        super.updateSubtask();
    }

    @Test
    void createSubtask() {
        super.createSubtask();
    }

    @Test
    void createSubtaskWithId() {
        super.createSubtaskWithId();
    }

    @Test
    void createEpic() {
        super.createEpic();
    }

    @Test
    void createEpicWithId() {
        super.createEpicWithId();
    }

    @Test
    void deleteTaskById() {

        super.deleteTaskById();
    }

    @Test
    void deleteAllTasks() {
        super.deleteAllTasks();
    }

    @Test
    void createStandardTask() {
        super.testCreateStandardTask();
    }
}
