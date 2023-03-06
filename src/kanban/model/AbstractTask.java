package kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class AbstractTask implements Comparable<AbstractTask> {
    private String title;
    private String description;
    private int id;
    private Status status;
    private long duration;
    private LocalDateTime startTime;


    public AbstractTask(String title, String description, int id, LocalDateTime startTime, long duration) {
        this.title = title;
        this.description = description;
        this.id = id;
        status = Status.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public AbstractTask(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
        status = Status.NEW;
    }

    public boolean changeStatus() {
        Status currentStatus = this.status;

        if (this.status == Status.NEW) this.status = Status.IN_PROGRESS;
        else this.status = Status.DONE;
        if (this.status != currentStatus) return true;
        else return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTask task = (AbstractTask) o;
        return id == task.id
                && title.equals(task.title)
                && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status);
    }

    @Override
    public String toString() {
        String dateTimetask = "";
        if (startTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            dateTimetask = ", время начала: '" + startTime.format(formatter) + ", окончание: "
                    + getEndTime().format(formatter) + '\'';
        }

        return "Задача: { название: '" + title + '\''
                + ", описание: '" + description + '\''
                + ", id = " + id
                + dateTimetask
                + ", статус: '" + status
                + '\'';
    }

    @Override
    public int compareTo(AbstractTask anotherTask) {
        if (this.startTime == null) {
            return -1;
        }
        if (anotherTask.startTime == null) {
            return -1;
        }
        int result = this.startTime.compareTo(anotherTask.startTime);
        if (result != 0) {
            return result;
        }
        if (this.status == anotherTask.getStatus() && this.id == anotherTask.getId()) {
            return 0;
        } else if (this.status == Status.NEW && anotherTask.getStatus() == Status.IN_PROGRESS
                || this.status == Status.IN_PROGRESS && anotherTask.getStatus() == Status.DONE
                || this.status == Status.NEW && anotherTask.getStatus() == Status.DONE
                || this.id < anotherTask.getId()) {
            return -1;
        } else {
            return 1;
        }
    }

    public boolean isOverlap(AbstractTask otherTask) {
        if (this.startTime == null || otherTask.startTime == null) {
            return false;
        }
        if (this.startTime.isEqual(otherTask.startTime)) {
            return true;
        }
        if (this.startTime.isBefore(otherTask.startTime)) {
            //Duration timeDuration = Duration.between(startTime, otherTask.getEndTime());
            if (otherTask.startTime.isBefore(this.getEndTime())) {
                return true;
            }
        } else if (otherTask.startTime.isBefore(this.startTime)) {
            if (this.startTime.isBefore(otherTask.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }
}
