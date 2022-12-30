import java.util.List;
import java.util.Map;

public class TaskRepository implements TaskRegistry {

    /**
     * @param id
     * @param taskMap
     * @param task
     * @return taskMap
     */
    @Override
    public Map<Integer, AbstractTask> addTask(int id, Map<Integer, AbstractTask> taskMap, AbstractTask task) {
        taskMap.put(id, task);
        return taskMap;
    }

    /**
     * @param taskMap
     * @return taskMap
     */
    @Override
    public Map<Integer, AbstractTask> deleteAllTasks(Map<Integer, AbstractTask> taskMap) {
        taskMap.clear();
        return taskMap;
    }

    /**
     * @param id
     * @param taskMap
     * @return taskMap
     */
    @Override
    public Map<Integer, AbstractTask> deleteTaskById(int id, Map<Integer, AbstractTask> taskMap) {
        taskMap.remove(id);
        return taskMap;
    }

    /**
     * @param taskMap
     * @param task
     * @return taskMap
     */
    @Override
    public Map<Integer, AbstractTask> updateTask(Map<Integer, AbstractTask> taskMap, AbstractTask task) {
        taskMap.put(task.getId(), task);
        return taskMap;
    }

    /**
     * @param id
     * @param taskMap
     * @return task
     */
    @Override
    public AbstractTask getTaskById(int id, Map<Integer, AbstractTask> taskMap) {
        return taskMap.get(id);
    }
}
