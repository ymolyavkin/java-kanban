package kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends AbstractTask {
    private final int parentId;

    public int getParentId() {
        return parentId;
    }

    public Subtask(String title, String description, int id, int parentId, LocalDateTime startTime, Duration duration) {
        super(title, description, id, startTime, duration);
        this.parentId = parentId;
    }
    public Subtask(String title, String description, int id, int parentId) {
        super(title, description, id);
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return super.toString() + ", parentId = " + parentId + " }";
    }
}
