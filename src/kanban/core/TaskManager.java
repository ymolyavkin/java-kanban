package kanban.core;

import kanban.model.AbstractTask;
import kanban.model.EpicTask;
import kanban.model.Subtask;
import kanban.model.Task;

import java.util.Map;

public interface TaskManager {
    QueueTask getHistory();
    void addEpic(EpicTask epicTask);

    Task createStandardTask(String titleAndDescription);

    boolean updateStandardTask(Task task, String[] newTitleAndDescription, boolean mustChangeStatus);

    Subtask updateSubtask(Subtask subtask, String[] newTitleAndDescription, boolean mustChangeStatus);

    Subtask createSubtask(String titleAndDescription, int parentId);

    EpicTask createEpic(String titleAndDescription);

    EpicTask addSubtaskToEpic(EpicTask epicTask, Subtask subtask);

    Map<Integer, AbstractTask> getStandardTasks();

    Map<Integer, AbstractTask> getEpicTasks();

    AbstractTask findTaskByIdOrNull(int id);

    boolean deleteTaskById(int id);

    boolean deleteAllTasks();
}
