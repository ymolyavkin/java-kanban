import java.util.HashMap;
import java.util.Map;

public class EpicTask extends AbstractTask {
    private Map<Integer, Task> subtasks;

    public EpicTask(String title, String description, int id) {
        super(title, description, id);
        this.subtasks = new HashMap<>();
    }

    public void addSubtask(int id, Task task) {
        subtasks.put(id, task);
    }

    /**
     *
     */
    @Override
    void changeStatus() {
        if (true)
        super.changeStatus();
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subtasks=" + subtasks +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
