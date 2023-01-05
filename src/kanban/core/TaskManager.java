package kanban.core;

import kanban.model.AbstractTask;
import kanban.model.EpicTask;
import kanban.model.Subtask;
import kanban.model.Task;

import java.util.*;

public final class TaskManager {
    private int taskId;
    private final Map<Integer, AbstractTask> standardTasks;
    private final Map<Integer, AbstractTask> epicTasks;
    private static TaskManager instance;


    private TaskManager() {
        taskId = 0;
        standardTasks = new HashMap<>();
        epicTasks = new HashMap<>();
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public void addEpic(EpicTask epicTask) {
        epicTasks.put(epicTask.getId(), epicTask);
    }


    public Task createStandardTask(String titleAndDescription) {
        String[] parts = titleAndDescription.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int id = taskId;
        taskId++;

        Task task = new Task(title, description, id);
        standardTasks.put(id, task);
        return task;
    }

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

    public Subtask updateSubtask(Subtask subtask, String[] newTitleAndDescription, boolean mustChangeStatus) {
        subtask.setTitle(newTitleAndDescription[0]);
        subtask.setDescription(newTitleAndDescription[1]);

        if (mustChangeStatus) {
            subtask.changeStatus();
        }
        return subtask;
    }

    public Subtask createSubtask(String titleAndDescription, int parentId) {
        String[] parts = titleAndDescription.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int id = taskId;
        taskId++;

        Subtask subtask = new Subtask(title, description, id, parentId);
        return subtask;
    }

    public EpicTask createEpic(String titleAndDescription) {
        String[] parts = titleAndDescription.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int epicId = taskId;
        taskId++;

        EpicTask epicTask = new EpicTask(title, description, epicId);
        return epicTask;
    }

    public EpicTask addSubtaskEpic(EpicTask epicTask, Subtask subtask) {
        epicTask.addSubtask(subtask);
        // Меняем статус эпика, если изменились статусы всех подзадач
        epicTask.changeStatus();
        return epicTask;
    }

    public Map<Integer, AbstractTask> getStandardTasks() {
        return standardTasks;
    }

    public Map<Integer, AbstractTask> getEpicTasks() {
        return epicTasks;
    }

    public AbstractTask findTaskByIdOrNull(int id) {
        // Ищем среди обычных задач
        if (!standardTasks.isEmpty()) {
            if (standardTasks.containsKey(id)) {
                Task task = (Task) standardTasks.get(id);

                return task;
            }
        }
        //Ищем среди эпиков
        if (!epicTasks.isEmpty()) {
            if (epicTasks.containsKey(id)) {
                EpicTask epic = (EpicTask) epicTasks.get(id);

                return epic;
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
                Subtask subtask = subtasks.get(id);

                return subtask;
            }
        }
        return null;
    }


    public boolean deleteTaskById(int id) {
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

    public boolean deleteAllTasks() {
        if (standardTasks.isEmpty() && epicTasks.isEmpty()) {
            return false;
        } else {
            standardTasks.clear();
            epicTasks.clear();
            return true;
        }
    }
}
