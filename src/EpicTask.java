import java.util.HashMap;
import java.util.Map;

public class EpicTask extends AbstractTask {
    private Map<Integer, Task> subtasks;

    public EpicTask(String title, String description, int id, int parentId) {
        super(title, description, id, parentId);
        this.subtasks = new HashMap<>();
    }

    public void addSubtask(int id, Task task) {
        subtasks.put(id, task);
    }

    public void updateSubtask(int id, Task task) {
        subtasks.put(id, task);
    }

    public Map<Integer, Task> getSubtasks() {
        return subtasks;
    }

    @Override
    boolean changeStatus() {
        Status curerentStatus = this.getStatus();
        // Если хотя бы одна из подзадач в статусе IN_PROGRESS, то статус эпика должен быть IN_PROGRESS
        if (oneStatusIsProgress(subtasks) && curerentStatus == Status.IN_PROGRESS) {
            return false; //Статус задачи не изменился
        } else if (oneStatusIsProgress(subtasks) && curerentStatus == Status.NEW) {
            this.setStatus(Status.IN_PROGRESS);
            return true; //Статус задачи изменился
        }
        // Если все подзадачи в статусе DONE, то статус эпика должен быть DONE
        // Достаем любой элемент из Мар
        Map.Entry<Integer, Task> entry = subtasks.entrySet().iterator().next();
        Task someTask = entry.getValue();
        if (someTask.getStatus() == Status.DONE && allStatusesIsEqual(subtasks) && curerentStatus != Status.DONE) {
            this.setStatus(Status.DONE);
            return true;
        }
        return false;
    }



    private boolean allStatusesIsEqual(Map<Integer, Task> tasks) {
        if (tasks.isEmpty()) return true;
        // Достаем любой элемент из Мар
        Map.Entry<Integer, Task> entry = tasks.entrySet().iterator().next();
        Status currentStatus = entry.getValue().getStatus();
        // обходим всю мапу перебирая её значения
        for (Task value : tasks.values()) {
            if (currentStatus != value.getStatus()) return false;
        }
        return true;
    }

    private boolean oneStatusIsProgress(Map<Integer, Task> tasks) {
        if (tasks.isEmpty()) return false;
        // обходим всю мапу перебирая её значения
        for (Task value : tasks.values()) {
            if (value.getStatus() == Status.IN_PROGRESS) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Эпик { " +
                "название: '" + this.getTitle() + '\'' +
                ", description: '" + this.getDescription() + '\'' +
                ", id = " + this.getId() +
                ", статус: " + this.getStatus() + "}\n" +
                "Подзадачи: {\n" + printSubtasks(subtasks) +
                "}";
    }

    private String printSubtasks(Map<Integer, Task> subtasks) {
        StringBuilder result = new StringBuilder();
        for (Task task : subtasks.values()) {
            result.append("  ");
            result.append(task.toString());
            result.append("\n");
        }
        return result.toString();
    }
}
