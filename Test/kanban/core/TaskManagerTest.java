package kanban.core;

import kanban.model.AbstractTask;
import kanban.model.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

//import static jdk.jpackage.internal.IOUtils.deleteRecursive;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


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

    @BeforeEach
    public void prepareTestData() {
        task = new Task(title, description, id, startTime, duration);
    }

    @Test
    public void addTask() {
        //task = new Task(title, description, id, startTime, duration);

        standardTasks.put(id, task);
        allTasksSorted.add(task);

        assertEquals(1, standardTasks.size());
        assertEquals(1, allTasksSorted.size());
    }

    @Test
    void createStandardTask() {
        LocalDateTime dateTime = LocalDateTime.of(2023, Month.JANUARY, 01, 8, 0);
        Task task = new Task("title", "description", 0, dateTime, 15);

        assertNotNull(task);
        assertEquals("title", task.getTitle());
        assertEquals("description", task.getDescription());
        assertEquals(dateTime, task.getStartTime());
        assertEquals(15, task.getDuration());
    }

    private static File dir;
    private static Path path;

    @Before
    public static void beforeClass() throws IOException {
        dir = Files.createTempDirectory(null).toFile();
    }

    @After
    public static void afterClass() throws IOException {
        if (path == null) {
            return;
        }
        //  deleteRecursive(path);
    }
    /*@Test
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
    }*/


}
