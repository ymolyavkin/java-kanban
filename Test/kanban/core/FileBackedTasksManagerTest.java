package kanban.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest<T extends TaskManager> extends TaskManagerTest {
    private Path path;

    @BeforeEach
    void setUp() {
        path = Path.of("taskbacket.txt");
        taskManager = new FileBackedTasksManager(path);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void setNeedWriteToFile() {
    }

    @Test
    void loadFromFile() {
    }

    @Test
    void readFileOrNull() {
    }

    @Test
    void addEpic() {
    }

    @Test
    void testAddTask() {
    }

    @Test
    void updateStandardTask() {
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
}