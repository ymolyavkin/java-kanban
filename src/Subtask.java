public class Subtask extends AbstractTask {
    private int parentId;

    public Subtask(String title, String description, int id, int parentId) {
        super(title, description, id);
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return super.toString() + " parentId=" + parentId + '}';
    }
}
