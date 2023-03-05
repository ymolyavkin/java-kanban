package kanban.model;

import java.time.LocalDateTime;

public final class Task extends AbstractTask {
    public Task(String title, String description, int id, LocalDateTime startTime, long duration) {
        super(title, description, id, startTime, duration);
    }

    @Override
    public String toString() {
        return super.toString() + " }";
    }
}
