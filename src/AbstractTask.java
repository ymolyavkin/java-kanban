import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTask {
    protected String title;
    protected String description;
    protected int id;
    protected Status status;
    protected Map<Integer, AbstractTask> taskMap;

    public AbstractTask(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
        status = Status.NEW;
        taskMap=new HashMap<>();
    }

    public int getId() {
        return id;
    }
}
