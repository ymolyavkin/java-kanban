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

    public AbstractTask updateTitleAndDescription(AbstractTask task) {
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
            task.setDescription(description);
        }
        return task;
    }

    public Task updateTaskItem(Task task) {
        task = (Task) updateTitleAndDescription(task);
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


    public boolean getListOfAllTasks() {
        if (standardTasks.isEmpty() && epicTasks.isEmpty()) {
            return false;
        }
        if (!standardTasks.isEmpty()) {
            System.out.println("Список обычных задач");
            for (Map.Entry<Integer, AbstractTask> entry : standardTasks.entrySet()) {
                int id = entry.getKey();
                Task task = (Task) entry.getValue();
                System.out.println(task + " ");
            }
        }
        if (!epicTasks.isEmpty()) {
            System.out.println("Список Эпиков");
            for (Map.Entry<Integer, AbstractTask> entry : epicTasks.entrySet()) {
                int id = entry.getKey();
                EpicTask task = (EpicTask) entry.getValue();
                System.out.println(task + " ");
            }
        }
        return true;
    }


    public boolean findTaskById(int id) {
        // Ищем среди обычных задач
        if (!standardTasks.isEmpty()) {
            if (standardTasks.containsKey(id)) {
                Task task = (Task) standardTasks.get(id);
                System.out.println(task);
                return true;
            }
        }
        //Ищем среди эпиков
        if (!epicTasks.isEmpty()) {
            if (epicTasks.containsKey(id)) {
                EpicTask epicTask = (EpicTask) epicTasks.get(id);
                System.out.println(epicTask);
                return true;
            }
        }
        // Ищем среди подзадач
        for (AbstractTask abstractTask : epicTasks.values()) {
            // Получаем эпик
            EpicTask epic = (EpicTask) abstractTask;
            // Получаем подзадачи эпика
            Map<Integer, Task> subtasks = epic.getSubtasks();
            // Ищем среди подзадач текущего эпика
            if (subtasks.containsKey(id)) {
                Task task = (Task) subtasks.get(id);
                System.out.println(task);
                return true;
            }
        }
        return false;
    }


    public void updateTaskById(int id) {
        if (standardTasks.containsKey(id)) {
            // обновляем текущую задачу
            Task currentTask = (Task) standardTasks.get(id);
            Task newTask = updateTaskItem(currentTask);
            // кладём обновленную задачу обратно в HashMap
            standardTasks.put(id, newTask);
        } else if (epicTasks.containsKey(id)) {
            System.out.println("Введенный id принадлежит эпику");
            EpicTask epicTask = (EpicTask) epicTasks.get(id);

            epicTask = (EpicTask) updateTitleAndDescription(epicTask);

            String[] menuItems = {"'Да' - '1', 'нет' - любая клавиша"};
            String userInput = userMenu("Будете ли менять подзадачи данного эпика?", menuItems);

            if (userInput.equals("1")) {
                System.out.println(epicTask);
                System.out.println("Введите id подзадачи");
                String strSubtaskId = scanner.nextLine();
                int subtaskId = Integer.parseInt(strSubtaskId);

                Map<Integer, Task> subtasks = epicTask.getSubtasks();
                if (subtasks.containsKey(subtaskId)) {
                    // Обновляем подзадачу
                    Task subtask = updateTaskItem(subtasks.get(subtaskId));
                    // Кладём подзадачу обратно в мапу
                    epicTask.updateSubtask(subtaskId, subtask);
                    // Меняем статус эпика, если изменились статусы всех подзадач
                    epicTask.changeStatus();
                    // Кладём обновлённый эпик в мапу epicTasks
                    epicTasks.put(id, epicTask);
                }
            }
        } else {
            System.out.println("Такого задачи с таким id нет, либо это подзадача");
            System.out.println("Для изменения подзадачи измените родительский эпик");
        }
    }

    public boolean deleteTaskById(int id) {
        // Ищем среди обычных задач
        if (!standardTasks.isEmpty()) {
            if (standardTasks.containsKey(id)) {
                standardTasks.remove(id);
                return true;
            }
        }
        //Ищем среди эпиков
        if (!epicTasks.isEmpty()) {
            if (epicTasks.containsKey(id)) {
                epicTasks.remove(id);
                return true;
            }
        }
        // Ищем среди подзадач
        for (AbstractTask abstractTask : epicTasks.values()) {
            // Получаем эпик
            EpicTask epic = (EpicTask) abstractTask;
            // Получаем подзадачи эпика
            Map<Integer, Task> subtasks = epic.getSubtasks();
            // Ищем среди подзадач текущего эпика
            if (subtasks.containsKey(id)) {
                subtasks.remove(id);
                return true;
            }
        }
        return false;
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
