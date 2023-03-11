package kanban.core;

import kanban.exceptions.ManagerSaveException;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


//InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>
class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    //class FileBackedTasksManagerTest<T extends TaskManager> extends TaskManagerTest {
    private static Path tempFilePath;

    @BeforeEach
    void setUp() throws IOException {
        // path = Path.of("testtask.txt");
        taskManager = new FileBackedTasksManager(tempFilePath);
        tempFilePath = Files.createTempFile("testtask.txt", "");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void setNeedWriteToFile() {
    }

    @Test
    void loadFromFile() {
        List<String> tasks = new ArrayList<>();
        try {
            String multilineFromFile = readFileOrNull();

            if (multilineFromFile != null) {
                int poSeparator = multilineFromFile.indexOf(System.lineSeparator());
                multilineFromFile = multilineFromFile.substring(poSeparator + 2, multilineFromFile.length());
                int posEnd = multilineFromFile.indexOf("\r\n\r\n");
                if (posEnd != -1) {
                    String content = multilineFromFile.substring(0, posEnd);

                    tasks.addAll(Arrays.asList(content.split(System.lineSeparator())));
                    createTaskFromFile(tasks);

                    int posHystory = posEnd + 4;
                    String history = multilineFromFile.substring(posHystory, multilineFromFile.length());

                    if (!history.isEmpty()) {
                        List<Integer> idTaskFromHistory = historyFromFile(history);
                        addTasksToHistory(idTaskFromHistory);
                    }
                }

            }
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCreateFile() throws IOException {
        final Path tempFilePath = Files.createTempFile("testtask.txt", "");

        try (BufferedWriter writer = Files.newBufferedWriter(tempFilePath, StandardCharsets.UTF_8)) {
            if (Files.notExists(tempFilePath)) {
                writer.write("0,TASK,Title,Description,01.01.2023 08:00,20,NEW");
                //Files.writeString(tempFilePath, "0,TASK,Title,Description,01.01.2023 08:00,20,NEW");
            }
        }
        assertFalse(Files.notExists(tempFilePath), "Файл не создан");
    }

    @Test
    void shouldReadFile() {
        String content = null;

        try (BufferedWriter writer = Files.newBufferedWriter(tempFilePath, StandardCharsets.UTF_8)) {
            writer.write("0,TASK,Title,Description,01.01.2023 08:00,20,NEW");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            if (Files.exists(tempFilePath)) {
                content = Files.readString(tempFilePath);
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        assertEquals(content, "0,TASK,Title,Description,01.01.2023 08:00,20,NEW", "Файл прочитан неверно");
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