package kanban.core;

import kanban.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private static int initId;
    private static List<Integer> usedIds = new ArrayList<>();
    private static final Map<Integer, AbstractTask> standardTasks = new HashMap<>();
    private static final Map<Integer, AbstractTask> epicTasks = new HashMap<>();
    private static InMemoryTaskManager instance;
    private static InMemoryHistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();


    InMemoryTaskManager() {
        initId = 0;
    }

    public static InMemoryHistoryManager getInMemoryHistoryManager() {
        return inMemoryHistoryManager;
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

    public void addTask(Task task) {
        int id = task.getId();
        standardTasks.put(id, task);
    }

    public void addEpic(EpicTask epicTask) {

        epicTasks.put(epicTask.getId(), epicTask);
    }

    /**
     * @return Task
     */
    private int generateId() {
        fillListAllIds();
        if (usedIds.isEmpty()) {
            return initId++;
        }
        int taskId = 0;
        boolean idIsBusy = true;
        while (idIsBusy) {
            idIsBusy = false;
            for (Integer id : usedIds) {
                if (taskId == id) {
                    idIsBusy = true;
                    taskId++;
                    break;
                }
            }
        }
        return taskId;
    }

    private void fillListAllIds() {
        if (!standardTasks.isEmpty()) {
            for (int key : standardTasks.keySet()) {
                usedIds.add(key);
            }
        }
        if (!epicTasks.isEmpty()) {
            for (AbstractTask epicTask : epicTasks.values()) {
                EpicTask epic = (EpicTask) epicTask;
                usedIds.add(epic.getId());
                Map<Integer, Subtask> subtasks = epic.getSubtasks();

                for (int key : subtasks.keySet()) {
                    usedIds.add(key);
                }
            }
        }
       // return usedIds;
    }

    public Task createStandardTask(String titleAndDescription) {
        String[] parts = titleAndDescription.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int id = generateId();
        Type type = Type.TASK;
        Task task = new Task(type, title, description, id);

        addTask(task);
        return task;
    }

    public Task createStandardTaskWithId(int id, String title, String description) {
        Type type = Type.TASK;
        Task task = new Task(type, title, description, id);

        addTask(task);
        return task;
    }

    public void updateStandardTask(Task task, String[] newTitleAndDescription, boolean mustChangeStatus) {
        task.setTitle(newTitleAndDescription[0]);
        task.setDescription(newTitleAndDescription[1]);
        boolean statusWasChanged = false;
        if (mustChangeStatus) {
            statusWasChanged = task.changeStatus();
        }
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
        int id = generateId();
        Type type = Type.SUBTASK;

        Subtask subtask = new Subtask(type, title, description, id, parentId);
        return subtask;
    }

    public Subtask createSubtaskWithId(int id, String title, String description, int parentId) {
        Type type = Type.SUBTASK;
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
        int epicId = generateId();
        Type type = Type.EPIC;

        EpicTask epicTask = new EpicTask(type, title, description, epicId);
        return epicTask;
    }

    public EpicTask createEpicWithId(int id, String title, String description) {
        int epicId = generateId();
        Type type = Type.EPIC;

        EpicTask epicTask = new EpicTask(type, title, description, id);
        return epicTask;
    }

    // public EpicTask addSubtaskToEpic(EpicTask epicTask, Subtask subtask) {
    public void addSubtaskToEpic(EpicTask epicTask, Subtask subtask) {
        epicTask.addSubtask(subtask);
        // Меняем статус эпика, если изменились статусы всех подзадач
        epicTask.changeStatus();
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
