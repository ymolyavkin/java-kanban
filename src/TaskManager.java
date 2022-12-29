public final class TaskManager {
    private int taskId = 0;

    public static TaskManager getInstance() {
        return TaskManagerHolder.instance;
    }

    private TaskManager() {
    }

    private static final class TaskManagerHolder {
        static final TaskManager instance = new TaskManager();
    }
    public void getListOfAllTasks() {
        System.out.println("getListOfAllTasks");
    }

    public void createTask() {

        System.out.println("createTask");
    }

    public void findTaskById(int id) {
        System.out.println("findTaskById");
    }

    public void updateTaskById(int id) {
        System.out.println("updateTaskById");
    }

    public void deleteTaskById(int id) {
        System.out.println("deleteTaskById");
    }

    public void deleteAllTasks() {
        System.out.println("deleteAllTasks");
    }


}
