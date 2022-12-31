import java.util.HashMap;
import java.util.Map;

public class EpicTask extends AbstractTask {
    private Map<Integer, StandardTask> subtasks;

    public EpicTask(String title, String description, int id) {
        super(title, description, id);
        this.subtasks = new HashMap<>();
    }

    public void addSubtask(int id, StandardTask task) {
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
}
