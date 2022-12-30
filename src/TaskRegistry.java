import java.util.List;
import java.util.Map;

public interface TaskRegistry {
    Map<Integer, AbstractTask> addTask(int id, Map<Integer, AbstractTask> taskMap, AbstractTask task);

    Map<Integer, AbstractTask> deleteAllTasks(Map<Integer, AbstractTask> taskMap);

    Map<Integer, AbstractTask> deleteTaskById(int id, Map<Integer, AbstractTask> taskMap);

    Map<Integer, AbstractTask> updateTask(Map<Integer, AbstractTask> taskMap, AbstractTask task);

    AbstractTask getTaskById(int id, Map<Integer, AbstractTask> taskMap);
}
