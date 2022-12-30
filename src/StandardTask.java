import java.util.Map;

public class StandardTask extends AbstractTask {
    private Map<Integer, StandardTask> taskMap;
    public StandardTask(String title, String description, int id) {
        super(title, description, id);
        System.out.println("From Standard Task");
    }
}
