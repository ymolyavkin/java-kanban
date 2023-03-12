package kanban.core;

import kanban.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static int initId;
    private static List<Integer> usedIds = new ArrayList<>();
    private static final Map<Integer, AbstractTask> standardTasks = new HashMap<>();
    private static final Map<Integer, AbstractTask> epicTasks = new HashMap<>();
    private static final TreeSet<AbstractTask> allTasksSorted = new TreeSet<>();
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

        int idOver = idOverlap(task);
        if (idOver != -1) {
            System.out.println("Task " + id + " intersects with task " + idOver);
        } else {
            System.out.println("This is no overlap");
        }

        standardTasks.put(id, task);
        allTasksSorted.add(task);
    }

    public void addEpic(EpicTask epicTask) {
        epicTasks.put(epicTask.getId(), epicTask);
        allTasksSorted.add(epicTask);
    }

    /**
     * @return Task
     */
    private int generateId(int busyId) {
        fillListAllIds();
        if (busyId != -1) {
            usedIds.add(busyId);
        }
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
        if (standardTasks.isEmpty() && epicTasks.isEmpty()) {
            usedIds.clear();
            initId = 0;
            return;
        }
        if (!standardTasks.isEmpty()) {
            for (int key : standardTasks.keySet()) {
                usedIds.add(key);
            }
        }
        if (!epicTasks.isEmpty()) {
            for (AbstractTask epicTask : epicTasks.values()) {
                EpicTask epic = (EpicTask) epicTask;
                usedIds.add(epic.getId());
                //Map<Integer, Subtask> subtasks = epic.getSubtasks();
                TreeSet<Subtask> subtasks = epic.getSubtasks();
                for (Subtask subtask : subtasks) {
                    usedIds.add(subtask.getId());
                }
                /*for (int key : subtasks.keySet()) {
                    usedIds.add(key);
                }*/
            }
        }
    }

    public Task createStandardTask(String titleDescriptionTime) {
        String[] parts = titleDescriptionTime.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int id = generateId(-1);

        LocalDateTime startTime = null;
        long duration = 0;

        if (!parts[2].equals("0")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            startTime = LocalDateTime.parse(parts[2], formatter);
            duration = Integer.parseInt(parts[3]);
        }

        Task task = new Task(title, description, id, startTime, duration);

        addTask(task);
        return task;
    }

    private static int idOverlap(Task task) {
        for (AbstractTask itemTask : allTasksSorted) {
            if (itemTask.isOverlap(task)) {
                return itemTask.getId();
            }
        }
        return -1;
    }

    public Task restoreStandardTaskWithId(int id,
                                          String title,
                                          String description,
                                          LocalDateTime startTime,
                                          long duration,
                                          Status status) {
        Task task = new Task(title, description, id, startTime, duration);
        if (task.getStatus() != status) {
            task.setStatus(status);
        }

        addTask(task);
        return task;
    }

    // TODO: 06.03.2023 после обновления времени задачи нужно удалять ее из treeset и обратно добавлять
    // TODO: 09.03.2023 написать удаление из treeset by id
    public void updateStandardTask(Task task, String[] newTitleAndDescription, String[] newTime, boolean mustChangeStatus) {
        task.setTitle(newTitleAndDescription[0]);
        task.setDescription(newTitleAndDescription[1]);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        if (newTime[0] != "") {
            LocalDateTime newStartTime = LocalDateTime.parse(newTime[0], formatter);
            long newDuration = Long.parseLong(newTime[1]);

            task.setStartTime(newStartTime);
            task.setDuration(newDuration);
        }
        boolean statusWasChanged = false;
        if (mustChangeStatus) {
            statusWasChanged = task.changeStatus();
        }
    }


    public void updateEpic(EpicTask epic, String[] newTitleAndDescription) {
        epic.setTitle(newTitleAndDescription[0]);
        epic.setDescription(newTitleAndDescription[1]);
    }

    /**
     * @return Subtask after update
     */
    public Subtask updateSubtask(Subtask subtask, String[] newTitleAndDescription, String[] newTime, boolean mustChangeStatus) {
        subtask.setTitle(newTitleAndDescription[0]);
        subtask.setDescription(newTitleAndDescription[1]);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime newStartTime = LocalDateTime.parse(newTime[0], formatter);
        long newDuration = Long.parseLong(newTime[1]);

        subtask.setStartTime(newStartTime);
        subtask.setDuration(newDuration);

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
        int id = generateId(parentId);

        usedIds.add(id);

        LocalDateTime startTime = null;
        long duration = 0;
        if (!parts[2].equals("0")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            startTime = LocalDateTime.parse(parts[2], formatter);
            duration = Integer.parseInt(parts[3]);
        }

        Subtask subtask = new Subtask(title, description, id, parentId, startTime, duration);
        return subtask;
    }

    public Subtask createSubtaskWithId(int id,
                                       String title,
                                       String description,
                                       int parentId,
                                       LocalDateTime startTime,
                                       long duration,
                                       Status status) {
        //  Type type = Type.SUBTASK;
        Subtask subtask = new Subtask(title, description, id, parentId, startTime, duration);

        if (subtask.getStatus() != status) {
            subtask.setStatus(status);
        }
        return subtask;
    }

    /**
     * @return Epictask after create
     */
    public EpicTask createEpic(String titleAndDescription) {
        String[] parts = titleAndDescription.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int epicId = generateId(-1);
        Type type = Type.EPIC;

        //EpicTask epicTask = new EpicTask(type, title, description, epicId);
        EpicTask epicTask = new EpicTask(title, description, epicId);
        return epicTask;
    }

    public EpicTask restoreEpicWithId(int id, String title, String description, Status status) {
        //, LocalDateTime startTime, long duration) {
        //  int epicId = generateId(-1);
        //Type type = Type.EPIC;

        //EpicTask epicTask = new EpicTask(type, title, description, id, startTime, duration);
        //EpicTask epicTask = new EpicTask(type, title, description, id);
        EpicTask epicTask = new EpicTask(title, description, id);

        if (epicTask.getStatus() != status) {
            epicTask.setStatus(status);
        }
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

    public TreeSet<AbstractTask> getPrioritizedTasks() {

        return allTasksSorted;
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

    /*public AbstractTask findTaskByIdSetOrNull(int id) {
        AbstractTask foundTask = null;
        Iterator<AbstractTask> iterator = allTasks.iterator();
        while (iterator.hasNext()) {
            AbstractTask task = iterator.next();
            if (task.getId() == id){
                foundTask=task;
                addTaskIntoHistory(foundTask);
            }
        }
        return foundTask;
    }*/

    public Subtask findSubtaskByIdOrNull(int id, TreeSet<Subtask> subtasks) {
        Iterator<Subtask> iterator = subtasks.iterator();
        while (iterator.hasNext()) {
            Subtask subtask = iterator.next();
            if (subtask.getId() == id)
                return subtask;
        }
        return null;
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
                TreeSet<Subtask> subtasks = epic.getSubtasks();
                foundTask = findSubtaskByIdOrNull(id, subtasks);
               /* Map<Integer, Subtask> subtasks = epic.getSubtasks();
                // Ищем среди подзадач текущего эпика
                if (subtasks.containsKey(id)) {

                    foundTask = subtasks.get(id);
                }*/
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
            //Map<Integer, Subtask> subtasks = epic.getSubtasks();
            TreeSet<Subtask> subtasks = epic.getSubtasks();
            // Ищем среди подзадач текущего эпика
            Subtask subtask = findSubtaskByIdOrNull(id, subtasks);
            if (subtask != null) {
                subtasks.remove(subtask);
                return true;
            }

            /*if (subtasks.containsKey(id)) {
                subtasks.remove(id);
                return true;
            }*/
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
            allTasksSorted.clear();
            return true;
        }
    }
}
