package kanban.core;

import kanban.exceptions.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
    void shouldRestoreTaskFromFile() {


    }

    static Stream<String> linesFromFile() {

        final String oneTaskInHistory = "id, type, name, description, status, startTime, duration, epic\r\n" +
                "0,TASK,Title,Description,01.01.2023 08:00,20,NEW\r\n\r\n0";
        final String noTask = "id, type, name, description, status, startTime, duration, epic\r\n\r\n";
        final String emptyHistory = "id, type, name, description, status, startTime, duration, epic\r\n" +
                "1,TASK,Физминутка,Выполнить десять приседаний,23.02.2023 12:24,15,NEW\r\n2,TASK,Почитать новости," +
                "Открыть мессенджер и просмотреть новые сообщения,24.02.2023 12:24,15,NEW\r\n\r\n";
        final String epicWithoutHistory = "id, type, name, description, status, startTime, duration, epic\r\n" +
                "3,EPIC,Прочитать почту,Прочитать все входящие письма и сообщения из мессенджеров,23.02.2023 " +
                "12:53,45,NEW\r\n\r\n";

        return Stream.of(oneTaskInHistory, noTask, emptyHistory, epicWithoutHistory);
    }

    @ParameterizedTest
    @MethodSource("linesFromFile")
    void restoreDataFromFile(String lineFromFile) {
        List<String> tasks = new ArrayList<>();
        try {
            String multilineFromFile = lineFromFile;

            if (multilineFromFile != null) {
                int poSeparator = multilineFromFile.indexOf(System.lineSeparator());
                multilineFromFile = multilineFromFile.substring(poSeparator + 2, multilineFromFile.length());

                int posEnd = multilineFromFile.indexOf("\r\n\r\n");
                if (posEnd != -1) {
                    String content = multilineFromFile.substring(0, posEnd);

                    tasks.addAll(Arrays.asList(content.split(System.lineSeparator())));
                    System.out.println("size = " + tasks.size());

                    switch (content.charAt(0)) {
                        case '0' -> {
                            System.out.println("0");
                            assertEquals(1, tasks.size());
                        }
                        case '1' -> {
                            System.out.println("1");
                            assertEquals(2, tasks.size());
                        }
                        case '3' -> {
                            System.out.println("3");
                            assertEquals(1, tasks.size());
                        }
                    }
                    int posHystory = posEnd + 4;
                    String history = multilineFromFile.substring(posHystory, multilineFromFile.length());

                    if (!history.isEmpty()) {
                        assertEquals("0", history);
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
        super
    }

    @Test
    void deleteAllTasks() {
    }
}