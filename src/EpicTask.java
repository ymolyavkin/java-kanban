import java.util.HashMap;
import java.util.Map;

public class EpicTask extends AbstractTask {
    private Map<Integer, StandardTask> subtasks;

    public EpicTask(String title, String description, int id) {
        super(title, description, id);
        this.subtasks = new HashMap<>();
    }
}
