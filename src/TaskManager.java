import java.util.*;

public final class TaskManager {
    private int taskId;
    private final Map<Integer, AbstractTask> standardTasks;
    private final Map<Integer, AbstractTask> epicTasks;

    private static TaskManager instance;


    private TaskManager() {
        taskId = 0;
        standardTasks = new HashMap<>();
        epicTasks = new HashMap<>();
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public void addEpic(EpicTask epicTask) {
        epicTasks.put(epicTask.getId(), epicTask);
    }


    public Task createStandardTask(String titleAndDescription) {
        String[] parts = titleAndDescription.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int id = taskId;
        taskId++;

        Task task = new Task(title, description, id);
        standardTasks.put(id, task);
        return task;
    }

    public boolean updateStandardTask(Task task, String[] newTitleAndDescription, boolean mustChangeStatus) {
        task.setTitle(newTitleAndDescription[0]);
        task.setDescription(newTitleAndDescription[1]);
        boolean statusWasChanged = false;
        if (mustChangeStatus) {
            statusWasChanged = task.changeStatus();
        }
        // кладём обновленную задачу обратно в HashMap
        standardTasks.put(task.getId(), task);

        return statusWasChanged;
    }

    public Subtask updateSubtask(Subtask subtask, String[] newTitleAndDescription, boolean mustChangeStatus) {
        subtask.setTitle(newTitleAndDescription[0]);
        subtask.setDescription(newTitleAndDescription[1]);

        if (mustChangeStatus) {
            subtask.changeStatus();
        }
        return subtask;
    }

    public Subtask createSubtask(String titleAndDescription, int parentId) {
        String[] parts = titleAndDescription.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int id = taskId;
        taskId++;

        Subtask subtask = new Subtask(title, description, id, parentId);
        return subtask;
    }

    public EpicTask createEpic(String titleAndDescription) {
        String[] parts = titleAndDescription.split("\\|");
        String title = parts[0];
        String description = parts[1];
        int epicId = taskId;
        taskId++;

        EpicTask epicTask = new EpicTask(title, description, epicId);
        return epicTask;
    }

    public EpicTask addSubtaskEpic(EpicTask epicTask, Subtask subtask) {
        epicTask.addSubtask(subtask);
        // Меняем статус эпика, если изменились статусы всех подзадач
        epicTask.changeStatus();
        return epicTask;
    }

    public Map<Integer, AbstractTask> getStandardTasks() {
        return standardTasks;
    }

    public Map<Integer, AbstractTask> getEpicTasks() {
        return epicTasks;
    }

    public AbstractTask findTaskById(int id) {
        // Ищем среди обычных задач
        if (!standardTasks.isEmpty()) {
            if (standardTasks.containsKey(id)) {
                Task task = (Task) standardTasks.get(id);

                return task;
            }
        }
        //Ищем среди эпиков
        if (!epicTasks.isEmpty()) {
            if (epicTasks.containsKey(id)) {
                EpicTask epic = (EpicTask) epicTasks.get(id);

                return epic;
            }
        }
        // Ищем среди подзадач
        for (AbstractTask abstractTask : epicTasks.values()) {
            // Получаем эпик
            EpicTask epic = (EpicTask) abstractTask;
            // Получаем подзадачи эпика
            Map<Integer, Subtask> subtasks = epic.getSubtasks();
            // Ищем среди подзадач текущего эпика
            if (subtasks.containsKey(id)) {
                Subtask subtask = subtasks.get(id);

                return subtask;
            }
        }
        return null;
    }


    /* public void updateTaskById(int id) {
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
                 int subtaskId = stringToInt(strSubtaskId);

                 Map<Integer, Subtask> subtasks = epicTask.getSubtasks();
                 if (subtasks.containsKey(subtaskId)) {
                     // Обновляем подзадачу
                     Subtask subtask = updateTaskItem(subtasks.get(subtaskId));

                     // Кладём подзадачу обратно в мапу
                     epicTask.updateSubtask(subtaskId, subtask);
                     // Меняем статус эпика, если изменились статусы всех подзадач
                     boolean statusChanged = epicTask.changeStatus();

                     if (statusChanged) {
                         System.out.print(Color.GREEN);
                         System.out.println("Статус эпика был изменён на " + epicTask.getStatus());
                         System.out.print(Color.RESET);
                     }
                     // Кладём обновлённый эпик в мапу epicTasks
                     epicTasks.put(id, epicTask);
                 }
             }
         } else {
             System.out.print(Color.RED);
             System.out.println("Задачи с таким id нет, либо это подзадача");
             System.out.println("Для изменения подзадачи измените родительский эпик");
             System.out.print(Color.RESET);
         }
     }
 */
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
            Map<Integer, Subtask> subtasks = epic.getSubtasks();
            // Ищем среди подзадач текущего эпика
            if (subtasks.containsKey(id)) {
                subtasks.remove(id);
                return true;
            }
        }
        return false;
    }

    public boolean deleteAllTasks() {
        if (standardTasks.isEmpty() && epicTasks.isEmpty()) {
            return false;
        } else {
            standardTasks.clear();
            epicTasks.clear();
            return true;
        }
    }
}
