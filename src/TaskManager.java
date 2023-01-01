import java.util.*;

public final class TaskManager {
    private int taskId;
    private final Scanner scanner;
    private final Map<Integer, AbstractTask> standardTasks;
    private final Map<Integer, AbstractTask> epicTasks;
    private final TaskRepository taskRepository;


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

    public Task createTaskItem(int parentId) {
        System.out.println("Введите название задачи");
        String title = scanner.nextLine();
        System.out.println("Введите описание задачи");
        String description = scanner.nextLine();

        int id = taskId;
        taskId++;

        Task task = new Task(title, description, id, parentId);
        return task;
    }

    public Task updateTaskItem(Task task) {
        String currentTitle = task.getTitle();
        System.out.println("Текущее название задачи:" + currentTitle);
        System.out.println("Новое название (если ввод будет пустым, то останется старое значение):");
        String title = scanner.nextLine();
        if (title == "") {
            System.out.println("Название не изменилось: " + currentTitle);
        } else {
            System.out.println("Новое название: " + title);
            task.setTitle(title);
        }
        String currentDescription = task.getDescription();
        System.out.println("Текущее описание задачи: " + currentDescription);
        System.out.println("Новое описание (если ввод будет пустым, то останется старое значение):");
        String description = scanner.nextLine();
        if (description == "") {
            System.out.println("Описание не изменилось: " + currentDescription);
        } else {
            System.out.println("Новое описание: " + description);
            task.setTitle(title);
        }
        String[] menuItems = {"Для изменения статуса нажмите '1'", "Оставить прежним - нажмите любую клавишу"};
        String userInput = userMenu("Следует изменить статус задачи?", menuItems);
        if (userInput.equals("1")) {
            task.changeStatus();
            System.out.println("Новый статус: " + task.getStatus());
        }

        return task;
    }

    public void createStandardTask() {
        System.out.println("createStandardTask");
        taskRepository.addTask(taskId, standardTasks, createTaskItem(-1));
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

        EpicTask epicTask = new EpicTask(title, description, epicId, -1);

        System.out.println("Ввод подзадач");
        int subtaskCount = 1;
        System.out.println("Для ввода " + subtaskCount + "-й подзадачи нажмите любую клавишу, '0' - закончить ввод");
        String userInput = scanner.nextLine();
        while (!userInput.equals("0")) {
            epicTask.addSubtask(taskId, createTaskItem(epicId));
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
        if (standardTasks.isEmpty() && epicTasks.isEmpty()) {
            System.out.print(Color.RED);
            System.out.println("У Вас нет задач");
            System.out.print(Color.RESET);
        } else {
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
    }


    public void findTaskById(int id) {
        System.out.println("findTaskById");
    }

    public void updateTaskById(int id) {
        //standardTasks;
        //epicTasks;
        if (standardTasks.containsKey(id)) {
            // обновляем текущую задачу
            Task currentTask = (Task) standardTasks.get(id);
            Task newTask = updateTaskItem(currentTask);
            // кладём обновленную задачу обратно в HashMap
            standardTasks.put(id, newTask);
        } /*else if (epicTasks.containsKey(id)) {
            taskRepository.updateTask(id, epicTasks, new Task());
        } else {
            System.out.println("Извините, такой команды пока нет.");
        }*/
    }

    public Task updateTask(int parentId) {
        return null;
    }

    public void deleteTaskById(int id) {
        System.out.println("deleteTaskById");
    }

    public void deleteAllTasks() {
        if (standardTasks.isEmpty() && epicTasks.isEmpty()) {
            System.out.print(Color.RED);
            System.out.println("У Вас нет задач");
            System.out.print(Color.RESET);
        } else {
            taskRepository.deleteAllTasks(standardTasks);
            taskRepository.deleteAllTasks(epicTasks);
        }

    }

}
