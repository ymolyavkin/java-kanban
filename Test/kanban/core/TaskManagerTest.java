package kanban.core;

import kanban.model.AbstractTask;
import kanban.model.Task;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

/*import static kanban.core.InMemoryTaskManager.allTasksSorted;
import static kanban.core.InMemoryTaskManager.standardTasks;*/
import static jdk.jpackage.internal.IOUtils.deleteRecursive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {
    final Map<Integer, AbstractTask> standardTasks = new HashMap<>();
    final TreeSet<AbstractTask> allTasksSorted = new TreeSet<>();
    String[] parts = new String[]{"Title", "Description", "21.03.2021 12:00", "15"};
    String title = parts[0];
    String description = parts[1];
    int id = 0;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    LocalDateTime startTime = LocalDateTime.parse(parts[2], formatter);
    long duration = Integer.parseInt(parts[3]);
    Task task;

    @Before
    public void prepareTestData() {
        task = new Task(title, description, id, startTime, duration);
    }
    @Test
    public void addTask() {
        task = new Task(title, description, id, startTime, duration);

        standardTasks.put(id, task);
        allTasksSorted.add(task);

        assertEquals(1, standardTasks.size());
        assertEquals(1, allTasksSorted.size());
    }

    @Test
    void createStandardTask() {
        Task task = new Task(title, description, id, startTime, duration);

        assertNotNull(task);

    }

    private static File dir;
    private static Path path;

    @BeforeClass
    public static void beforeClass() throws IOException {
        dir = Files.createTempDirectory(null).toFile();
    }

    @AfterClass
    public static void afterClass() throws IOException {
        if (path == null) {
            return;
        }
        deleteRecursive(path);
    }
    @Test
    public void testgetSurname() {
        System.out.println("get surname");
        String filename = "";
        String expResult = "";
        String result = fileReader.getSurname(filename);
        assertEquals(expResult, result);

        filename = "datafiles/names.txt";
        String data = "Jack";
        InputStream stdin = System.in;
        try{
            System.setIn(new ByteArrayInputStream(data.getBytes()));
            Scanner scanner = new Scanner(System.in);
            System.out.println(scanner.nextLine());
        } finally {
            System.setIn(stdin);
            expResult = "Davis";
        }
        String result = fileReader.getSurname(filename);
        assertEquals(expResult, result);
    }


}
