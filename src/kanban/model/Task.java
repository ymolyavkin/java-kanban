package kanban.model;
public final class Task extends AbstractTask {
    public Task(Type type, String title, String description, int id) {
        super(type, title, description, id);
    }

    @Override
    public String toString() {
        return super.toString() + " }";
    }
}
