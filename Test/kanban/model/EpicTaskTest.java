package kanban.model;


import org.junit.jupiter.api.Test;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {
    private TreeSet<Subtask> subtasks;
    private final String title = "Title";
    private final String description = "Description";
    private final int epicId = 0;
    final int firstId = 1;
    final int secondId = 2;
    final int thirdId = 3;

    @Test
    void testChangeStatusNoSubtask() {
        EpicTask epic = new EpicTask(title, description, epicId);
        epic.changeStatus();
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void testChangeStatusAllSubtasksNew() {
        EpicTask epic = new EpicTask(title, description, epicId);

        Subtask firstSubtask = new Subtask(title, description, firstId, epicId);
        Subtask secondSubtask = new Subtask(title, description, secondId, epicId);
        Subtask thirdSubtask = new Subtask(title, description, thirdId, epicId);

        epic.addSubtask(firstSubtask);
        epic.addSubtask(secondSubtask);
        epic.addSubtask(thirdSubtask);

        epic.changeStatus();

        assertEquals(Status.NEW, epic.getStatus());
    }
    @Test
    void testChangeStatusSubtasksNewDone() {
        EpicTask epic = new EpicTask(title, description, epicId);

        Subtask firstSubtask = new Subtask(title, description, firstId, epicId);
        Subtask secondSubtask = new Subtask(title, description, secondId, epicId);
        Subtask thirdSubtask = new Subtask(title, description, thirdId, epicId);

        epic.addSubtask(firstSubtask);
        epic.addSubtask(secondSubtask);
        epic.addSubtask(thirdSubtask);

        firstSubtask.setStatus(Status.DONE);

        epic.changeStatus();

        assertEquals(Status.NEW, epic.getStatus());
    }
    @Test
    void testChangeStatusAllSubtasksDone() {
        EpicTask epic = new EpicTask(title, description, epicId);

        Subtask firstSubtask = new Subtask(title, description, firstId, epicId);
        Subtask secondSubtask = new Subtask(title, description, secondId, epicId);
        Subtask thirdSubtask = new Subtask(title, description, thirdId, epicId);

        epic.addSubtask(firstSubtask);
        epic.addSubtask(secondSubtask);
        epic.addSubtask(thirdSubtask);

        firstSubtask.setStatus(Status.DONE);
        secondSubtask.setStatus(Status.DONE);
        thirdSubtask.setStatus(Status.DONE);

        epic.changeStatus();

        assertEquals(Status.DONE, epic.getStatus());
    }
    @Test
    void testChangeStatusAllSubtasksInProgress() {
        EpicTask epic = new EpicTask(title, description, epicId);

        Subtask firstSubtask = new Subtask(title, description, firstId, epicId);
        Subtask secondSubtask = new Subtask(title, description, secondId, epicId);
        Subtask thirdSubtask = new Subtask(title, description, thirdId, epicId);

        epic.addSubtask(firstSubtask);
        epic.addSubtask(secondSubtask);
        epic.addSubtask(thirdSubtask);

        firstSubtask.setStatus(Status.IN_PROGRESS);
        secondSubtask.setStatus(Status.IN_PROGRESS);
        thirdSubtask.setStatus(Status.IN_PROGRESS);

        epic.changeStatus();

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}