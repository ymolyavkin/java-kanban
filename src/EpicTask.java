import java.util.HashMap;
import java.util.Map;

public final class EpicTask extends AbstractTask {
    private final Map<Integer, Subtask> subtasks;

    public EpicTask(String title, String description, int id) {
        super(title, description, id);
        this.subtasks = new HashMap<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    /*public void updateSubtask(int id, Subtask subtask) {
        subtasks.put(id, subtask);
    }*/

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    boolean changeStatus() {
        Status currentStatus = this.getStatus();

        // Если хотя бы одна из подзадач в статусе IN_PROGRESS, то статус эпика должен быть IN_PROGRESS
        if (oneStatusIsProgress(subtasks) && currentStatus == Status.IN_PROGRESS) {
            return false; //Статус задачи не изменился
        } else if (oneStatusIsProgress(subtasks) && currentStatus == Status.NEW) {
            this.setStatus(Status.IN_PROGRESS);
            return true; //Статус задачи изменился
        }
        // Если все подзадачи в статусе DONE, то статус эпика должен быть DONE
        // Достаем любой элемент из Мар
        Map.Entry<Integer, Subtask> entry = subtasks.entrySet().iterator().next();
        Subtask someTask = entry.getValue();

        if (someTask.getStatus() == Status.DONE && allStatusesIsEqual(subtasks) && currentStatus != Status.DONE) {
            this.setStatus(Status.DONE);
            return true;
        }
        return false;
    }

    private boolean allStatusesIsEqual(Map<Integer, Subtask> tasks) {
        if (tasks.isEmpty()) return true;

        // Достаем любой элемент из Мар
        Map.Entry<Integer, Subtask> entry = tasks.entrySet().iterator().next();
        Status currentStatus = entry.getValue().getStatus();

        // обходим всю мапу перебирая её значения
        for (Subtask value : tasks.values()) {
            if (currentStatus != value.getStatus()) return false;
        }
        return true;
    }

    private boolean oneStatusIsProgress(Map<Integer, Subtask> tasks) {
        if (tasks.isEmpty()) return false;

        // обходим всю мапу перебирая её значения
        for (Subtask value : tasks.values()) {
            if (value.getStatus() == Status.IN_PROGRESS) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Эпик { " +
                "название: '" + this.getTitle() + '\'' +
                ", описание: '" + this.getDescription() + '\'' +
                ", id = " + this.getId() +
                ", статус: " + this.getStatus() + " }\n" +
                "Подзадачи: {\n" + printSubtasks(subtasks) +
                "}";
    }

    private String printSubtasks(Map<Integer, Subtask> subtasks) {
        StringBuilder result = new StringBuilder();

        for (Subtask subtask : subtasks.values()) {
            result.append("  ");
            result.append(subtask.toString());
            result.append("\n");
        }
        return result.toString();
    }
}
