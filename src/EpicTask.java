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
    void changeStatus() {
        //boolean mustChange = false;
        if (allStatusesIsEqual(subtasks)) {
            // Достаем любой элемент из Мар
            Map.Entry<Integer, Task> entry = subtasks.entrySet().iterator().next();
            Task someTask = entry.getValue();
            if (this.compareTo(someTask) == -1) {
                super.changeStatus();
                System.out.println("Статус эпика изменен в соответствии со статусами подзадач на " + this.getStatus());
            }
        }
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

    @Override
    public String toString() {
        return "Эпик { " +
                "название: '" + this.getTitle() + '\'' +
                ", description: '" + this.getDescription() + '\'' +
                ", id = " + this.getId() +
                ", статус: " + this.getStatus() + "}\n" +
                "{ Подзадача: " + subtasks +
                "}\n";
    }
}
