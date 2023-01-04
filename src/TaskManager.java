import java.util.*;

public final class TaskManager {
    private int taskId;
    //private final Scanner scanner;
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

        /**
         * String[] parts = titleAndDescription.split("\\|");
         *         String title = parts[0];
         *         String description = parts[1];
         *         int id = taskId;
         *         taskId++;
         *
         *         standardTasks.put(id, createStandardTask(title, description));
         *         return id;
         */

    }



    /*public AbstractTask updateTitleAndDescription(AbstractTask task, String newTitle, String newDescription) {
        String currentTitle = task.getTitle();

        System.out.println("Текущее название задачи: " + currentTitle);
        System.out.println("Новое название (если ввод будет пустым, то останется старое значение):");
        String title = scanner.nextLine();

        if (newTitle == "") {
            System.out.println("Название не изменилось: " + currentTitle);
        } else {
            System.out.println("Новое название: " + newTitle);
            task.setTitle(newTitle);
        }
        String currentDescription = task.getDescription();

        System.out.println("Текущее описание задачи: " + currentDescription);
        System.out.println("Новое описание (если ввод будет пустым, то останется старое значение):");
        String description = scanner.nextLine();

        if (newDescription == "") {
            System.out.println("Описание не изменилось: " + currentDescription);
        } else {
            System.out.println("Новое описание: " + newDescription);
            task.setDescription(newDescription);
        }
        return task;
    }
*/
   /* public Task updateTaskItem(Task task, String newTitle, String newDescription) {
        task = (Task) updateTitleAndDescription(task, newTitle, newDescription);

        String[] menuItems = {"Для изменения статуса нажмите '1'", "Оставить прежним - нажмите любую клавишу"};
        String userInput = userMenu("Следует изменить статус задачи?", menuItems);

        if (userInput.equals("1")) {
            task.changeStatus();
            System.out.println("Новый статус: " + task.getStatus());
        }
        return task;
    }*/

    public Subtask createSubtask(String title, String description, int parentId) {
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

    public EpicTask addSubtaskEpic(EpicTask epicTask, String titleAndDescription) {
        String[] parts = titleAndDescription.split("\\|");
        String title = parts[0];
        String description = parts[1];

        Subtask subtask = createSubtask(title, description, epicTask.getParentId());
        epicTask.addSubtask(subtask);

        return epicTask;
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
            Map<Integer, Subtask> subtasks = epic.getSubtasks();
            // Ищем среди подзадач текущего эпика
            if (subtasks.containsKey(id)) {
                Subtask subtask = subtasks.get(id);

                System.out.println(subtask);
                return true;
            }
        }
        return false;
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
