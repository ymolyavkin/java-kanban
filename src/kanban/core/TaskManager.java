package kanban.core;

import kanban.model.AbstractTask;
import kanban.model.EpicTask;
import kanban.model.Subtask;
import kanban.model.Task;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface TaskManager {
    List<AbstractTask> getHistory();

    void addEpic(EpicTask epicTask);
    void addTask(Task Task);

    Task createStandardTask(String titleAndDescription);

    boolean updateStandardTask(Task task, String[] newTitleAndDescription, String[] newTime, boolean mustChangeStatus);

    Subtask updateSubtask(Subtask subtask, String[] newTitleAndDescription, String[] newTime, boolean mustChangeStatus);
    void updateEpic(EpicTask epic, String[] newTitleAndDescription);
    Subtask createSubtask(String titleAndDescription, int parentId);

    EpicTask createEpic(String titleAndDescription);

    //EpicTask addSubtaskToEpic(EpicTask epicTask, Subtask subtask);
   // void addSubtaskToEpic(EpicTask epicTask, Subtask subtask);

    Map<Integer, AbstractTask> getStandardTasks();

    Map<Integer, AbstractTask> getEpicTasks();
    TreeSet<AbstractTask> getPrioritizedTasks();

    AbstractTask findTaskByIdOrNull(int id, boolean savedToHistory);

    boolean deleteTaskById(int id);

    boolean deleteAllTasks();
}
