package kanban.core;

import kanban.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private static int taskId;
    private static final Map<Integer, AbstractTask> standardTasks = new HashMap<>();
    private static final Map<Integer, AbstractTask> epicTasks = new HashMap<>();
    private static InMemoryTaskManager instance;
    private static InMemoryHistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    private static InFileHistoryManager inFileHistoryManager = Managers.getFromFileHistory();


    InMemoryTaskManager() {
        taskId = 0;
    }

    public static InMemoryTaskManager getInstance() {
        if (instance == null) {
            instance = new InMemoryTaskManager();

        }
        return instance;
    }

    /**
     * @return List<AbstractTask>
     */
    @Override
    public List<AbstractTask> getHistory() {

        return inMemoryHistoryManager.getHistory();
    }

    public void addEpic(EpicTask epicTask) {

        epicTasks.put(epicTask.getId(), epicTask);
    }

    /**
     * @return Task
     */
    public Task createStandardTask(String titleAndDescription) {
        String[] parts = titleAndDescription.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int id = taskId;
        Type type = Type.TASK;
        taskId++;

        Task task = new Task(type, title, description, id);
        standardTasks.put(id, task);
        return task;
    }

    /**
     * @return boolean statusWasChanged
     */
    public boolean updateStandardTask(Task task, String[] newTitleAndDescription, boolean mustChangeStatus) {
        task.setTitle(newTitleAndDescription[0]);
        task.setDescription(newTitleAndDescription[1]);
        boolean statusWasChanged = false;
        if (mustChangeStatus) {
            statusWasChanged = task.changeStatus();
        }
        // кладём обновленную задачу обратно в HashMap
        standardTasks.put(task.getId(), task);

        return statusWasChanged;
    }

    /**
     * @return Subtask after update
     */
    public Subtask updateSubtask(Subtask subtask, String[] newTitleAndDescription, boolean mustChangeStatus) {
        subtask.setTitle(newTitleAndDescription[0]);
        subtask.setDescription(newTitleAndDescription[1]);

        if (mustChangeStatus) {
            subtask.changeStatus();
        }
        return subtask;
    }

    /**
     * @return Subtask after create
     */
    public Subtask createSubtask(String titleAndDescription, int parentId) {
        String[] parts = titleAndDescription.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int id = taskId;
        Type type=Type.SUBTASK;
        taskId++;

        Subtask subtask = new Subtask(type, title, description, id, parentId);
        return subtask;
    }

    /**
     * @return Epictask after create
     */
    public EpicTask createEpic(String titleAndDescription) {
        String[] parts = titleAndDescription.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int epicId = taskId;
        Type type=Type.EPIC;
        taskId++;

        EpicTask epicTask = new EpicTask(type, title, description, epicId);
        return epicTask;
    }

    /**
     * @return EpicTask after adding
     */
    public EpicTask addSubtaskToEpic(EpicTask epicTask, Subtask subtask) {
        epicTask.addSubtask(subtask);
        // Меняем статус эпика, если изменились статусы всех подзадач
        epicTask.changeStatus();
        return epicTask;
    }

    /**
     * @return Map<Integer, AbstractTask> standardTasks
     */
    public Map<Integer, AbstractTask> getStandardTasks() {

        return standardTasks;
    }

    /**
     * @return Map<Integer, AbstractTask> EpicTasks
     */
    public Map<Integer, AbstractTask> getEpicTasks() {

        return epicTasks;
    }

    public void addTaskIntoHistory(AbstractTask task) {

        inMemoryHistoryManager.add(task);
        inFileHistoryManager.add(task);
    }

    public AbstractTask findTaskByIdOrNull(int id) {
        AbstractTask foundTask = null;
        // Ищем среди обычных задач
        if (standardTasks.containsKey(id)) {

            foundTask = standardTasks.get(id);

        } else if (epicTasks.containsKey(id)) {
            //Ищем среди эпиков
            foundTask = epicTasks.get(id);

        } else {
            // Ищем среди подзадач
            for (AbstractTask abstractTask : epicTasks.values()) {
                // Получаем эпик
                EpicTask epic = (EpicTask) abstractTask;
                // Получаем подзадачи эпика
                Map<Integer, Subtask> subtasks = epic.getSubtasks();
                // Ищем среди подзадач текущего эпика
                if (subtasks.containsKey(id)) {

                    foundTask = subtasks.get(id);
                }
            }
        }
        if (foundTask != null) {
            addTaskIntoHistory(foundTask);
        }
        return foundTask;
    }

    /**
     * @return boolean task was deleted
     */
    public boolean deleteTaskById(int id) {
        inMemoryHistoryManager.remove(id);

        // Ищем среди обычных задач
        if (!standardTasks.isEmpty()) {
            if (standardTasks.containsKey(id)) {
                standardTasks.remove(id);
                return true;
            }
        }
        //Ищем среди эпиков
        if (!epicTasks.isEmpty()) {
            if (epicTasks.containsKey(id)) {
                epicTasks.remove(id);
                return true;
            }
        }
        // Ищем среди подзадач
        for (AbstractTask abstractTask : epicTasks.values()) {
            // Получаем эпик
            EpicTask epic = (EpicTask) abstractTask;

            // Получаем подзадачи эпика
            Map<Integer, Subtask> subtasks = epic.getSubtasks();
            // Ищем среди подзадач текущего эпика
            if (subtasks.containsKey(id)) {
                subtasks.remove(id);
                return true;
            }
        }
        return false;
    }

    /**
     * @return boolean all tasks was deleted
     */
    public boolean deleteAllTasks() {
        inMemoryHistoryManager.removeAllNodes();

        if (standardTasks.isEmpty() && epicTasks.isEmpty()) {
            return false;
        } else {
            standardTasks.clear();
            epicTasks.clear();
            return true;
        }
    }
}
