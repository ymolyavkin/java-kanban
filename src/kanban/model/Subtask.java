package kanban.model;

public class Subtask extends AbstractTask {
    private final int parentId;

    public int getParentId() {
        return parentId;
    }

    public Subtask(Type type, String title, String description, int id, int parentId) {
        super(type, title, description, id);
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return super.toString() + ", parentId = " + parentId + " }";
    }
}
