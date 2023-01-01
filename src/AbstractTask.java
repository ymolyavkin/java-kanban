import java.util.Objects;

public abstract class AbstractTask implements Comparable<AbstractTask> {
    private int parentId;
    private String title;
    private String description;
    private int id;
    private Status status;


    public AbstractTask(String title, String description, int id, int parentId) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.parentId = parentId;
        status = Status.NEW;
    }

    void changeStatus() {
        if (this.status == Status.NEW) this.status = Status.IN_PROGRESS;
        else this.status = Status.DONE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTask task = (AbstractTask) o;
        return id == task.id && title.equals(task.title) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status);
    }

    @Override
    public String toString() {
        return " Задача {" +
                "название: '" + title + '\'' +
                ", описание: '" + description + '\'' +
                ", id=" + id +
                ", статус: " + status +
                '}';
    }

    @Override
    public int compareTo(AbstractTask anotherTask) {
        if (this.status == anotherTask.getStatus()) {
            return 0;
        } else if (this.status == Status.NEW && anotherTask.getStatus() == Status.IN_PROGRESS
                || this.status == Status.IN_PROGRESS && anotherTask.getStatus() == Status.DONE
                || this.status == Status.NEW && anotherTask.getStatus() == Status.DONE) {
            return -1;
        } else {
            return 1;
        }

    }


    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
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

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
}
