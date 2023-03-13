package kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;

public final class EpicTask extends AbstractTask {
    private final TreeSet<Subtask> subtasks;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;

    public EpicTask(String title, String description, int id) {

        super(title, description, id);
        this.subtasks = new TreeSet<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public TreeSet<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public boolean changeStatus() {
        Status currentStatus = this.getStatus();
        if (subtasks.isEmpty()) {
            return false;
        }
        // Если хотя бы одна из подзадач в статусе IN_PROGRESS, то статус эпика должен быть IN_PROGRESS
        if (oneStatusIsProgress(subtasks) && currentStatus == Status.IN_PROGRESS) {
            return false; //Статус задачи не изменился
        } else if (oneStatusIsProgress(subtasks) && currentStatus == Status.NEW) {
            this.setStatus(Status.IN_PROGRESS);
            return true; //Статус задачи изменился
        }
        // Если все подзадачи в статусе DONE, то статус эпика должен быть DONE
        // Достаем любой элемент

        Subtask someTask = subtasks.first();

        if (someTask.getStatus() == Status.DONE && allStatusesIsEqual(subtasks) && currentStatus != Status.DONE) {
            this.setStatus(Status.DONE);
            return true;
        }
        return false;
    }

    private boolean allStatusesIsEqual(TreeSet<Subtask> tasks) {
        if (tasks.isEmpty()) return true;

        // Достаем любой элемент
        Subtask someTask = subtasks.first();
        Status currentStatus = someTask.getStatus();

        for (Subtask value : subtasks) {
            if (currentStatus != value.getStatus()) return false;
        }
        return true;
    }

    private boolean oneStatusIsProgress(TreeSet<Subtask> tasks) {
        if (tasks.isEmpty()) return false;

        for (Subtask task : tasks) {
            if (task.getStatus() == Status.IN_PROGRESS) return true;
        }
        return false;
    }

    public void calculateTime() {
        if (!subtasks.isEmpty()) {
            startTime = subtasks.first().getStartTime();
            endTime = null;
            LocalDateTime startLastTask = subtasks.last().getStartTime();
            if (startLastTask != null) {
                endTime = startLastTask.plusMinutes(subtasks.last().getDuration().toMinutes());
            }

            Duration totalDuration = Duration.ZERO;
            for (Subtask subtask : subtasks) {
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
            duration = totalDuration;
        }
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        String dateTimetask = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        if (getStartTime() != null && getEndTime() != null) {
            dateTimetask = ", время начала: '" + getStartTime().format(formatter) + ", окончание: "
                    + getEndTime().format(formatter) + '\'';
        }
        return "Эпик { "
                + "название: '" + this.getTitle() + '\''
                + ", описание: '" + this.getDescription() + '\''
                + ", id = " + this.getId()
                + dateTimetask
                + ", статус: " + this.getStatus() + " }\n"
                + "Подзадачи: {\n" + printSubtasks(subtasks)
                + "}";
    }

    private String printSubtasks(TreeSet<Subtask> subtasks) {
        StringBuilder result = new StringBuilder();

        for (Subtask subtask : subtasks) {
            result.append("  ");
            result.append(subtask.toString());
            result.append("\n");
        }
        return result.toString();
    }
}
