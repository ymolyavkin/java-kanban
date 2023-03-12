package kanban.core;

import kanban.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;


abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    private static Map<Integer, AbstractTask> standardTasks = new HashMap<>();
    private static Map<Integer, AbstractTask> epicTasks = new HashMap<>();
    private static TreeSet<AbstractTask> allTasksSorted = new TreeSet<>();

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
    }

    @Test
    void testCreateStandardTask() {
        LocalDateTime dateTime = LocalDateTime.of(2023, Month.JANUARY, 01, 8, 0);
        Task task = taskManager.createStandardTask("Title|Description|01.01.2023 08:00|20");

        final AbstractTask savedTask = taskManager.findTaskByIdOrNull(task.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        final Map<Integer, AbstractTask> tasks = taskManager.getStandardTasks();

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
        task.setStatus(Status.NEW);
        boolean mustChangeStatus = true;
        boolean statusWasChanged = false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime newStartTime = LocalDateTime.parse(newTime[0], formatter);
        long newDuration = Long.parseLong(newTime[1]);

        taskManager.updateStandardTask(task, newTitleAndDescription, newTime, mustChangeStatus);

        assertEquals("New title", task.getTitle());
        assertEquals("New description", task.getDescription());
        assertEquals(newStartTime, task.getStartTime());
        assertEquals(newDuration, task.getDuration());
        assertEquals(Status.IN_PROGRESS, task.getStatus());

        if (mustChangeStatus) {
            statusWasChanged = task.changeStatus();
        }
        assertEquals(Status.DONE, task.getStatus());
        assertEquals(statusWasChanged, true);
    }

    @Test
    void createEpic() {
        String titleAndDescription = title + "|" + description;
        EpicTask epicTask = taskManager.createEpic(titleAndDescription);

        assertNotNull(epicTask);
        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
    }

    @Test
    void updateEpic() {
        String[] newTitleAndDescription = {"New title", "New description"};

        taskManager.updateEpic(epic, newTitleAndDescription);
    }

    @Test
    void createSubtask() {
        String titleAndDescription = title + "|" + description;
        Subtask subtask = taskManager.createSubtask(titleAndDescription, parentId);

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


        /*subtask.setTitle(newTitleAndDescription[0]);
        subtask.setDescription(newTitleAndDescription[1]);*/

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime newStartTime = LocalDateTime.parse(newTime[0], formatter);
        long newDuration = Long.parseLong(newTime[1]);

        /*subtask.setStartTime(newStartTime);
        subtask.setDuration(newDuration);*/
        taskManager.updateSubtask(subtask, newTitleAndDescription, newTime, mustChangeStatus);

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
        var standard = taskManager.getStandardTasks();
        var sorted = taskManager.getPrioritizedTasks();
        int standardSize = standard.size();
        int sortedSize = sorted.size();

        taskManager.deleteTaskById(idTask);

        assertEquals(standardSize - 1, standard.size());
        assertEquals(sortedSize - 1, sorted.size());
    }

    @Test
    void deleteAllTasks() {
        var standard = taskManager.getStandardTasks();
        var epics = taskManager.getEpicTasks();
        var sorted = taskManager.getPrioritizedTasks();

        taskManager.deleteAllTasks();

        assertEquals(0, standard.size());
        assertEquals(0, epics.size());
        assertEquals(0, sorted.size());
    }
}
