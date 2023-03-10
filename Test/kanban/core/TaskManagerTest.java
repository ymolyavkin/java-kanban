package kanban.core;

import kanban.model.*;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


abstract class TaskManagerTest<T extends TaskManager> {
    final Map<Integer, AbstractTask> standardTasks = new HashMap<>();
    final TreeSet<AbstractTask> allTasksSorted = new TreeSet<>();
    final String[] parts = new String[]{"Title", "Description", "21.03.2021 12:00", "15"};
    final String title = parts[0];
    final String description = parts[1];
    final int id = 0;
    final int parentId = 1;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    LocalDateTime startTime = LocalDateTime.parse(parts[2], formatter);
    long duration = Integer.parseInt(parts[3]);
    Task task;
    Subtask subtask;
    EpicTask epic;

    @BeforeEach
    public void prepareTestData() {
        task = new Task(title, description, id, startTime, duration);
        subtask = new Subtask(title, description, id, parentId, startTime, duration);
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
    void testCreateStandardTask() {
        LocalDateTime dateTime = LocalDateTime.of(2023, Month.JANUARY, 01, 8, 0);
        Task task = new Task("title", "description", 0, dateTime, 15);

        assertNotNull(task);
        assertEquals("title", task.getTitle());
        assertEquals("description", task.getDescription());
        assertEquals(dateTime, task.getStartTime());
        assertEquals(15, task.getDuration());
    }

    @Test
    void createStandardTaskWithId() {
        LocalDateTime dateTime = LocalDateTime.of(2023, Month.JANUARY, 01, 8, 0);
        Status status = Status.IN_PROGRESS;
        Task task = new Task("title", "description", 0, dateTime, 15);

        assertNotNull(task);
        assertEquals("title", task.getTitle());
        assertEquals("description", task.getDescription());
        assertEquals(dateTime, task.getStartTime());
        assertEquals(15, task.getDuration());
        assertEquals(Status.NEW, task.getStatus());

        if (task.getStatus() != status) {
            task.setStatus(status);
        }
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    void updateStandardTask() {

        String[] newTitleAndDescription = {"New title", "New description"};
        String[] newTime = {"22.03.2021 15:00", "25"};
        boolean mustChangeStatus = true;
        boolean statusWasChanged = false;

        task.setTitle(newTitleAndDescription[0]);
        task.setDescription(newTitleAndDescription[1]);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime newStartTime = LocalDateTime.parse(newTime[0], formatter);
        long newDuration = Long.parseLong(newTime[1]);

        task.setStartTime(newStartTime);
        task.setDuration(newDuration);


        assertEquals("NewTitle", task.getTitle());
        assertEquals("NewDescription", task.getDescription());
        assertEquals(newStartTime, task.getStartTime());
        assertEquals(newDuration, task.getDuration());
        assertEquals(Status.NEW, task.getStatus());

        if (mustChangeStatus) {
            statusWasChanged = task.changeStatus();
        }

        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    void updateEpic() {
        String[] newTitleAndDescription = {"New title", "New description"};
        epic.setTitle(newTitleAndDescription[0]);
        epic.setDescription(newTitleAndDescription[1]);
    }

    @Test
    void createSubtaskWithId(int id,
                             String title,
                             String description,
                             int parentId,
                             LocalDateTime startTime,
                             long duration,
                             Status status) {
        //  Type type = Type.SUBTASK;
        Subtask subtask = new Subtask(title, description, id, parentId, startTime, duration);

        if (subtask.getStatus() != status) {
            subtask.setStatus(status);
        }
    }

    @Test
    void updateSubtask() {
        String[] newTitleAndDescription = {"New title", "New description"};
        String[] newTime = {"22.03.2021 15:00", "25"};
        boolean mustChangeStatus = true;

        subtask.setTitle(newTitleAndDescription[0]);
        subtask.setDescription(newTitleAndDescription[1]);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime newStartTime = LocalDateTime.parse(newTime[0], formatter);
        long newDuration = Long.parseLong(newTime[1]);

        subtask.setStartTime(newStartTime);
        subtask.setDuration(newDuration);

        assertEquals("New title", subtask.getTitle());
        assertEquals("New description", subtask.getDescription());
        assertEquals(newStartTime, subtask.getStartTime());
        assertEquals(newDuration, subtask.getDuration());
        assertEquals(Status.NEW, subtask.getStatus());

        if (mustChangeStatus) {
            subtask.changeStatus();
        }
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
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
