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
}
