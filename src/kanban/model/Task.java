package kanban.model;
public final class Task extends AbstractTask {
    public Task(String title, String description, int id) {
        super(title, description, id);
    }

    @Override
    public String toString() {
        return super.toString() + " }";
    }
}
