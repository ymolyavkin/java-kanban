package kanban.core;

import kanban.model.*;
import kanban.visual.Color;

import java.time.Duration;
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
        /*int idOver = -1;
        if (task.getStartTime() != null) {

            idOver = idOverlap(task);
        }
        printOverlapMessage(id, idOver);*/

        standardTasks.put(id, task);
        allTasksSorted.add(task);
    }

    private static void printOverlapMessage(int idTask, int idOverlapTask) {
        if (idOverlapTask != -1) {
            System.out.print(Color.RED);
            System.out.println("Задача " + idTask + " пересекается по времени с задачей " + idOverlapTask);
            System.out.print(Color.RESET);
        } else {
            System.out.print(Color.GREEN);
            System.out.println("Пересечений по времени нет");
            System.out.print(Color.RESET);
        }
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

    private void deleteIdFromListAllIds(int id) {
        if (usedIds.contains(id)) {
            usedIds.remove(id);
        }
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

                TreeSet<Subtask> subtasks = epic.getSubtasks();
                for (Subtask subtask : subtasks) {
                    usedIds.add(subtask.getId());
                }
            }
        }
    }

    public Task createStandardTask(String titleDescriptionTime) {
        String[] parts = titleDescriptionTime.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int id = generateId(-1);

        LocalDateTime startTime = null;
        Duration duration = null;

        if (!parts[2].equals("0")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            startTime = LocalDateTime.parse(parts[2], formatter);
            Long minutes = Long.parseLong(parts[3]);
            duration = Duration.ofMinutes(minutes);
        }

        Task task = new Task(title, description, id, startTime, duration);

        if (notOverlap(task)) {
            addTask(task);
            return task;
        } else {
            deleteIdFromListAllIds(id);
            return null;
        }
    }

    private static boolean notOverlap(AbstractTask task) {
        if (task.getStartTime() == null) {
            printOverlapMessage(task.getId(), -1);
            return true;
        } else {
            int idOver = idOverlap(task);
            printOverlapMessage(task.getId(), idOver);
            return idOver == -1;
        }
    }

    private static int idOverlap(AbstractTask task) {
        for (AbstractTask itemTask : allTasksSorted) {
            if (itemTask.getStartTime() == null) {
                continue;
            }
            if (itemTask instanceof EpicTask) {
                TreeSet<Subtask> subtasks = ((EpicTask) itemTask).getSubtasks();
                for (Subtask subtask : subtasks) {
                    if (task.isOverlap(subtask)) {
                        return subtask.getId();
                    }
                }
            } else if (itemTask.isOverlap(task)) {
                return itemTask.getId();
            }
        }
        return -1;
    }

    public Task restoreStandardTaskWithId(int id,
                                          String title,
                                          String description,
                                          LocalDateTime startTime,
                                          Duration duration,
                                          Status status) {
        Task task = new Task(title, description, id, startTime, duration);
        if (task.getStatus() != status) {
            task.setStatus(status);
        }
        /*int idOver = -1;
        if (task.getStartTime() != null) {
            idOver = idOverlap(task);
        }
        if (idOver == -1) {
            addTask(task);
        }
        printOverlapMessage(id, idOver);*/
        if (notOverlap(task)) {
            addTask(task);
            return task;
        }

        // addTask(task);
        return null;
    }


    public boolean updateStandardTask(Task task, String[] newTitleAndDescription, String[] newTime, boolean mustChangeStatus) {
        task.setTitle(newTitleAndDescription[0]);
        task.setDescription(newTitleAndDescription[1]);

        boolean statusWasChanged = false;
        if (mustChangeStatus) {
            statusWasChanged = task.changeStatus();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        if (newTime[0] != "") {
            LocalDateTime newStartTime = LocalDateTime.parse(newTime[0], formatter);
            Duration newDuration = Duration.parse(newTime[1]);

            task.setStartTime(newStartTime);
            task.setDuration(newDuration);

           /* int idOver = -1;
            if (task.getStartTime() != null) {
                idOver = idOverlap(task);
            }
            if (idOver == -1) {
                addTask(task);
            }
            printOverlapMessage(id, idOver);*/
            if (notOverlap(task)) {
                addTask(task);
                return true;
            } else {
                return false;
            }
        }
        return true;
    }


    public void updateEpic(EpicTask epic, String[] newTitleAndDescription) {
        epic.setTitle(newTitleAndDescription[0]);
        epic.setDescription(newTitleAndDescription[1]);
    }

    /**
     * @return Subtask after update
     */
    public Subtask updateSubtask(Subtask subtask, String[] newTitleAndDescription, String[] newTime, boolean mustChangeStatus) {
        Subtask oldSubtask = subtask;
        subtask.setTitle(newTitleAndDescription[0]);
        subtask.setDescription(newTitleAndDescription[1]);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime newStartTime = LocalDateTime.parse(newTime[0], formatter);
        Duration newDuration = Duration.parse(newTime[1]);

        subtask.setStartTime(newStartTime);
        subtask.setDuration(newDuration);

        if (mustChangeStatus) {
            subtask.changeStatus();
        }
        if (notOverlap(subtask)) {
            return subtask;
        }
        return oldSubtask;
    }

    /**
     * @return Subtask after create
     */
    public Subtask createSubtask(String titleAndDescription, int parentId) {
        Subtask subtask = null;
        String[] parts = titleAndDescription.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int id = generateId(parentId);

        usedIds.add(id);

        LocalDateTime startTime = null;
        Duration duration = null;
        if (parts.length == 4) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            startTime = LocalDateTime.parse(parts[2], formatter);
            long minutes = Integer.parseInt(parts[3]);
            duration = Duration.ofMinutes(minutes);

            subtask = new Subtask(title, description, id, parentId, startTime, duration);

            if (notOverlap(subtask)) {
                return subtask;
            } else {
                return null;
            }
        } else if (parts.length == 2) {
            subtask = new Subtask(title, description, id, parentId);
        }
        return subtask;
    }

    public Subtask createSubtaskWithId(int id,
                                       String title,
                                       String description,
                                       int parentId,
                                       LocalDateTime startTime,
                                       Duration duration,
                                       Status status) {

        Subtask subtask = new Subtask(title, description, id, parentId, startTime, duration);

        if (!notOverlap(subtask)) {
            return null;
        }
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

        EpicTask epicTask = new EpicTask(title, description, epicId);
        return epicTask;
    }

    public EpicTask restoreEpicWithId(int id, String title, String description, Status status) {

        EpicTask epicTask = new EpicTask(title, description, id);

        if (epicTask.getStatus() != status) {
            epicTask.setStatus(status);
        }
        return epicTask;
    }


    public void addSubtaskToEpic(EpicTask epicTask, Subtask subtask) {
       /* if (subtask.getStartTime() != null) {
            int id = subtask.getId();
            int idOver = idOverlap(subtask);

            printOverlapMessage(id, idOver);
        }*/
        if (subtask != null) {
            epicTask.addSubtask(subtask);
            // Меняем статус эпика, если изменились статусы всех подзадач
            epicTask.changeStatus();
        }
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
                var removalTask = standardTasks.get(id);

                standardTasks.remove(id);
                allTasksSorted.remove(removalTask);
                return true;
            }
        }
        //Ищем среди эпиков
        if (!epicTasks.isEmpty()) {
            if (epicTasks.containsKey(id)) {
                var removalEpic = epicTasks.get(id);
                epicTasks.remove(id);
                allTasksSorted.remove(removalEpic);
                return true;
            }
        }
        // Ищем среди подзадач
        for (AbstractTask abstractTask : epicTasks.values()) {
            // Получаем эпик
            EpicTask epic = (EpicTask) abstractTask;

            // Получаем подзадачи эпика
            TreeSet<Subtask> subtasks = epic.getSubtasks();
            // Ищем среди подзадач текущего эпика
            Subtask subtask = findSubtaskByIdOrNull(id, subtasks);
            if (subtask != null) {
                subtasks.remove(subtask);
                // allTasksSorted.remove(subtask);
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
            allTasksSorted.clear();
            return true;
        }
    }
}
