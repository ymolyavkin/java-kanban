import java.util.*;

public final class TaskManager {
    private int taskId;
    private Scanner scanner;
    private Map<Integer, AbstractTask> standardTasks;
    private Map<Integer, AbstractTask> epicTasks;
    private TaskRepository taskRepository;


    public static TaskManager getInstance() {
        return TaskManagerHolder.instance;
    }

    private TaskManager() {
        taskId = 0;
        standardTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        scanner = new Scanner(System.in);
        taskRepository = new TaskRepository();
    }

    private static final class TaskManagerHolder {
        static final TaskManager instance = new TaskManager();
    }

    private String userMenu(String text, String[] menuItems) {
        System.out.println(text + ": ");
        for (String menuItem : menuItems) {
            System.out.println(menuItem);
        }
        return scanner.nextLine();
    }

    public void getListOfAllSubtasks() {
        System.out.println("getListOfAllSubtasks");
    }

    public void createSubtask() {

        System.out.println("createSubtask");
    }

    public void findSubtaskById(int id) {
        System.out.println("findSubtaskById");
    }

    public void updateSubtaskById(int id) {

        System.out.println("updateSubtaskById");
    }

    public void deleteSubtaskById(int id) {

        System.out.println("deleteSubtaskById");
    }

    public void deleteAllSubtasks() {

        System.out.println("deleteAllSubtasks");
    }

    public void getListOfAllEpics() {
        System.out.println("getListOfAllEpics");
    }


    public void findEpicById(int id) {
        System.out.println("findEpicById");
    }

    public void updateEpicById(int id) {

        System.out.println("updateEpicById");
    }

    public void deleteEpicById(int id) {

        System.out.println("deleteEpicById");
    }

    public void deleteAllEpics() {

        System.out.println("deleteAllEpics");
    }

    public void getListOfAllStandardTasks() {
        System.out.println("getListOfAllTasks");
    }

    public void createTask() {
        System.out.println("createTask");
        String[] menuItems = {"1 - обычная задача", "2 - эпик"};
        String userInput = userMenu("Выберите тип создаваемой задачи:", menuItems);
        if (userInput.equals("1")) {
            createStandardTask();
        } else if (userInput.equals("2")) {
            createEpic();
        } else {
            System.out.println("Извините, такой команды пока нет.");
        }
    }

    public Task createTaskItem() {
        System.out.println("Введите название задачи");
        String title = scanner.nextLine();
        System.out.println("Введите описание задачи");
        String description = scanner.nextLine();

        int id = taskId;
        taskId++;

        Task task = new Task(title, description, id);
        return task;
    }

    public void createStandardTask() {
        System.out.println("createStandardTask");
        /*System.out.println("Введите название задачи");
        String title = scanner.nextLine();
        System.out.println("Введите описание задачи");
        String description = scanner.nextLine();

        int id = taskId;
        taskId++;

        Task task = new Task(title, description, id);*/

        taskRepository.addTask(taskId, standardTasks, createTaskItem());
        //standardTasks.put(id, standardTask);
        System.out.println("Создана стадартная задача с id = " + (taskId - 1));
        System.out.println();
    }

    public void createEpic() {
        System.out.println("createEpic");
        System.out.println("Введите название эпика");
        String title = scanner.nextLine();
        System.out.println("Введите описание эпика");
        String description = scanner.nextLine();

        int epicId = taskId;
        taskId++;

        EpicTask epicTask = new EpicTask(title, description, epicId);

        System.out.println("Ввод подзадач");
        //String userInput;
        int subtaskCount = 1;
        System.out.println("Для ввода " + subtaskCount + "-й подзадачи нажмите любую клавишу, '0' - закончить ввод");
        String userInput = scanner.nextLine();
        while (!userInput.equals("0"))
        {
            epicTask.addSubtask(taskId, createTaskItem());
            subtaskCount++;
            System.out.println("Для ввода " + subtaskCount + "-й подзадачи нажмите любую клавишу, '0' - закончить ввод");
            userInput = scanner.nextLine();
        }
        epicTasks.put(epicId, epicTask);
        System.out.println("Создан эпик с id = " + epicId);
    }

    public void findStandardTaskById(int id) {


        System.out.println("findTaskById");
    }

    public void updateStandardTaskById(int id) {
        System.out.println("updateStandardTaskById");
    }

    public void deleteStandardTaskById(int id) {
        System.out.println("deleteStandardTaskById");
    }

    public void deleteAllStandardTasks() {
        System.out.println("deleteAllStandardTasks");
    }

    public void getListOfAllTasks() {
        System.out.println("Список обычных задач");
        for (Map.Entry<Integer, AbstractTask> entry : standardTasks.entrySet()) {
            int id = entry.getKey();
            Task task = (Task) entry.getValue();
            System.out.println(id + " -> " + task);
        }
        System.out.println("Список Эпиков");
        for (Map.Entry<Integer, AbstractTask> entry : epicTasks.entrySet()) {
            int id = entry.getKey();
            EpicTask task = (EpicTask) entry.getValue();
            System.out.println(id + " -> " + task);
        }
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
