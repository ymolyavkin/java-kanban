public class Subtask extends AbstractTask {
    private AbstractTask parentEpic;

    public Subtask(String title, String description, int id, int parentId) {
        super(title, description, id, parentId);
    }


    @Override
    void changeStatus() {
        super.changeStatus();
    }
}
