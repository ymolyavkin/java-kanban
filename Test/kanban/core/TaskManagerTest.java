package kanban.core;

import kanban.model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {
    @Test
    void createStandardTask() {
        String[] parts = new String[]{"Title", "Description", "21.03.2021 12:00", "15"};
        String title = parts[0];
        String description = parts[1];
        int id = 0;

        LocalDateTime startTime = null;
        long duration = 0;

        if (!parts[2].equals("0")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            startTime = LocalDateTime.parse(parts[2], formatter);
            duration = Integer.parseInt(parts[3]);
        }

        Task task = new Task(title, description, id, startTime, duration);

        assertNotNull(task);

    }
}
