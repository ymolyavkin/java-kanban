import java.util.HashMap;
import java.util.Map;

public class EpicTask extends AbstractTask {
    private Map<Integer, Task> subtasks;

    public EpicTask(String title, String description, int id, int parentId) {
        super(title, description, id, parentId);
        this.subtasks = new HashMap<>();
    }

    public void addSubtask(int id, Task task) {
        subtasks.put(id, task);
    }

    @Override
    void changeStatus() {
        if (true)
            super.changeStatus();
    }

    @Override
    public String toString() {
        return "Эпик {" +
                ", название: '" + this.getTitle() + '\'' +
                ", description: '" + this.getDescription() + '\'' +
                ", id = " + this.getId() +
                ", статус: " + this.getStatus() +
                "подзадача: " + subtasks +
                '}';
    }
}
