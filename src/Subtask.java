public class Subtask extends AbstractTask {
    private AbstractTask parentEpic;

    public Subtask(String title, String description, int id) {
        super(title, description, id);
    }

    /**
     *
     */
    @Override
    void changeStatus() {
        super.changeStatus();
    }
}
