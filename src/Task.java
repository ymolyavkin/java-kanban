import java.util.Map;

public class Task extends AbstractTask {
    private Map<Integer, Task> taskMap;
    public Task(String title, String description, int id) {
        super(title, description, id);
    }
}
