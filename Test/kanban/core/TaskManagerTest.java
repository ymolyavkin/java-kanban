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

import static org.junit.jupiter.api.Assertions.*;


abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    private static final Map<Integer, AbstractTask> standardTasks = new HashMap<>();
    private static final Map<Integer, AbstractTask> epicTasks = new HashMap<>();
    private static final TreeSet<AbstractTask> allTasksSorted = new TreeSet<>();

    final String[] parts = new String[]{"Title", "Description", "21.03.2021 12:00", "15"};
    final String title = parts[0];
    final String description = parts[1];
    final int id = 0;
    final int epicId = 1;
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
        epic = new EpicTask(title, description, id);
        subtask = new Subtask(title, description, id, parentId, startTime, duration);
        epic.addSubtask(subtask);

        standardTasks.put(task.getId(), task);
        epicTasks.put(epic.getId(), epic);
        allTasksSorted.add(task);
        allTasksSorted.add(epic);
    }

    /*@Test
    public void addTask() {
        //task = new Task(title, description, id, startTime, duration);

       // standardTasks.put(id, task);
        allTasksSorted.add(task);

       // assertEquals(1, standardTasks.size());
        assertEquals(1, allTasksSorted.size());
    }*/

    @Test
    void testCreateStandardTask() {
        LocalDateTime dateTime = LocalDateTime.of(2023, Month.JANUARY, 01, 8, 0);
        Task task = taskManager.createStandardTask("Title|Description|01.01.2023 08:00|20");

        final AbstractTask savedTask = taskManager.findTaskByIdOrNull(task.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        final Map<Integer,AbstractTask> tasks = taskManager.getStandardTasks();

        assertNotNull(tasks, "Задачи не сохраняются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertTrue(tasks.containsValue(task), "Задачи не совпадают");


        assertNotNull(task);
        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(dateTime, task.getStartTime());
        assertEquals(20, task.getDuration());
    }

    @Test
    void restoreStandardTaskWithId() {
        LocalDateTime dateTime = LocalDateTime.of(2023, Month.JANUARY, 01, 8, 0);
        Status status = Status.IN_PROGRESS;
        Task task = taskManager.createStandardTask("title|description|01.01.2023 08:00|15");

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


        assertEquals("New title", task.getTitle());
        assertEquals("New description", task.getDescription());
        assertEquals(newStartTime, task.getStartTime());
        assertEquals(newDuration, task.getDuration());
        assertEquals(Status.NEW, task.getStatus());

        if (mustChangeStatus) {
            statusWasChanged = task.changeStatus();
        }
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        assertEquals(statusWasChanged, true);
    }

    @Test
    void createEpic() {
        EpicTask epicTask =  new EpicTask(title, description, epicId);

        assertNotNull(epicTask);
        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
    }

    @Test
    void updateEpic() {
        String[] newTitleAndDescription = {"New title", "New description"};

        epic.setTitle(newTitleAndDescription[0]);
        epic.setDescription(newTitleAndDescription[1]);
    }
    @Test
    void createSubtask() {
        Subtask subtask = new Subtask(title, description, id, parentId, startTime, duration);

        assertNotNull(task);
        assertEquals("Title", subtask.getTitle());
        assertEquals("Description", subtask.getDescription());
        assertEquals(startTime, subtask.getStartTime());
        assertEquals(15, subtask.getDuration());
        assertEquals(Status.NEW, subtask.getStatus());
    }
    @Test
    void createSubtaskWithId() {
        Status status = Status.IN_PROGRESS;
        Subtask subtask = new Subtask(title, description, id, parentId, startTime, duration);

        assertNotNull(task);
        assertEquals("Title", subtask.getTitle());
        assertEquals("Description", subtask.getDescription());
        assertEquals(startTime, subtask.getStartTime());
        assertEquals(15, subtask.getDuration());
        assertEquals(Status.NEW, subtask.getStatus());

        if (subtask.getStatus() != status) {
            subtask.setStatus(status);
        }
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
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

    @Test
    void createEpicWithId() {
        EpicTask epicTask = new EpicTask(title, description, id);

        assertNotNull(epicTask);
        assertEquals("Title", epicTask.getTitle());
        assertEquals("Description", epicTask.getDescription());

        Status status = Status.IN_PROGRESS;

        if (epicTask.getStatus() != status) {
            epicTask.setStatus(status);
        }
    }
    @Test
    void deleteTaskById() {
        int idTask = 0;
        //taskManager.
        taskManager.deleteTaskById(idTask);
      //  inMemoryHistoryManager.remove(id);

        // Ищем среди обычных задач
        if (!standardTasks.isEmpty()) {
            if (standardTasks.containsKey(id)) {
                standardTasks.remove(id);

            }
        }
        //Ищем среди эпиков
        if (!epicTasks.isEmpty()) {
            if (epicTasks.containsKey(id)) {
                epicTasks.remove(id);

            }
        }
        // Ищем среди подзадач
        for (AbstractTask abstractTask : epicTasks.values()) {
            // Получаем эпик
            EpicTask epic = (EpicTask) abstractTask;

            // Получаем подзадачи эпика
            //Map<Integer, Subtask> subtasks = epic.getSubtasks();
            TreeSet<Subtask> subtasks = epic.getSubtasks();
            // Ищем среди подзадач текущего эпика
           // Subtask subtask = findSubtaskByIdOrNull(id, subtasks);
            if (subtask != null) {
                subtasks.remove(subtask);

            }

            /*if (subtasks.containsKey(id)) {
                subtasks.remove(id);
                return true;
            }*/
        }

    }

    /*@Before
    public static void beforeClass() throws IOException {
        dir = Files.createTempDirectory(null).toFile();
    }

    @After
    public static void afterClass() throws IOException {
        if (path == null) {
            return;
        }
        //  deleteRecursive(path);
    }*/
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
