import java.util.Objects;

public abstract class AbstractTask {
    protected String title;
    protected String description;
    protected int id;
    protected Status status;


    public AbstractTask(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
        status = Status.NEW;
    }

    void changeStatus() {
        if (this.status == Status.NEW) this.status=Status.IN_PROGRESS;
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
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public int getId() {
        return id;
    }


}
