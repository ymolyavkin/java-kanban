package kanban.core;

import kanban.exceptions.ManagerSaveException;
import kanban.model.*;
import kanban.visual.Color;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
<<<<<<< HEAD
import java.nio.file.Paths;
=======
>>>>>>> 13659d98c5f6edb00b3079086ec5946aa2d6eac4
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path path;
<<<<<<< HEAD

    //java.nio.charset.Charset charset = StandardCharsets.UTF_8;
    private static FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Path.of("taskbacket.txt"));
    public FileBackedTasksManager(Path path) {
        super();
        this.path = path;
=======
    private static HistoryManager historyManager;

    private static final FileBackedTasksManager fileBackedTasksManager
            = new FileBackedTasksManager(Path.of("taskbacket.txt"), getInMemoryHistoryManager());

    public FileBackedTasksManager(Path path, HistoryManager historyManager) {
        super();
        this.path = path;
        this.historyManager = historyManager;
>>>>>>> 13659d98c5f6edb00b3079086ec5946aa2d6eac4
    }

    public static void main(String[] args) throws ManagerSaveException {

        System.out.println("From file backed manager");
<<<<<<< HEAD
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Path.of("taskbacket.txt"));
        fileBackedTasksManager.createSeveralTestTasksInFile();
=======
        //FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Path.of("taskbacket.txt"));
        fileBackedTasksManager.createSeveralTestTasksInFile();
        fileBackedTasksManager.findTaskByIdOrNull(0);
        fileBackedTasksManager.findTaskByIdOrNull(1);


>>>>>>> 13659d98c5f6edb00b3079086ec5946aa2d6eac4
        try {
            fileBackedTasksManager.save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }

<<<<<<< HEAD
    //

=======
>>>>>>> 13659d98c5f6edb00b3079086ec5946aa2d6eac4
    private static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager tasksFromFile = loadFromFile(file);
        return tasksFromFile;
    }

<<<<<<< HEAD
=======
    private static String historyToString(HistoryManager manager) {
        List<AbstractTask> browsingHistory = manager.getHistory();
        List<Integer> taskIds = taskIdsFromHistory(browsingHistory);
        StringBuilder sb = new StringBuilder();
        for (Integer id : taskIds) {
            sb.append(id + ",");
        }
        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private static List<Integer> taskIdsFromHistory(List<AbstractTask> browsingHistory) {
        List<Integer> taskIds = new ArrayList<>();

        for (AbstractTask task : browsingHistory) {
            taskIds.add(task.getId());
        }
        return taskIds;
    }

    private static List<Integer> historyFromString(String value) {

        return null;
    }


  /*  @Override
    public void addSubtaskToEpic(EpicTask epicTask, Subtask subtask) {
        super.addSubtaskToEpic(epicTask, subtask);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }

    }*/


>>>>>>> 13659d98c5f6edb00b3079086ec5946aa2d6eac4
    /**
     * метод save без параметров — он будет сохранять текущее состояние менеджера в файл,
     * который экземпляр данного клааса получает в конструкторе
     *
     * @throws IOException
     */
    private void save() throws ManagerSaveException {
        Map<Integer, AbstractTask> standardTasks = getStandardTasks();
        Map<Integer, AbstractTask> epicTasks = getEpicTasks();
        List<String> tasksInStringForm = tasksIntoListString(standardTasks);
        List<String> epicsInStringForm = tasksIntoListString(epicTasks);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
<<<<<<< HEAD
=======
            writer.write("id, type, name, description, status, epic \n");
>>>>>>> 13659d98c5f6edb00b3079086ec5946aa2d6eac4
            for (String s : tasksInStringForm) {
                writer.write(s);
            }
            for (String s : epicsInStringForm) {
                writer.write(s);
            }
<<<<<<< HEAD
=======
            writer.write("\n");
            writer.write(historyToString(historyManager));
>>>>>>> 13659d98c5f6edb00b3079086ec5946aa2d6eac4

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
<<<<<<< HEAD
=======

    }

    private void load() throws ManagerSaveException {
        try {
            Files.readString(path);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки файла");
        }
    }


    private String taskIdsFromHistoryInOneString(List<Integer> taskIds) {
        StringBuilder sb = new StringBuilder();
        for (Integer id : taskIds) {
            sb.append(String.valueOf(id));
        }
        return sb.toString();
>>>>>>> 13659d98c5f6edb00b3079086ec5946aa2d6eac4
    }

    private List<String> tasksIntoListString(Map<Integer, AbstractTask> tasks) {
        List<String> result = new ArrayList<>();
        for (AbstractTask task : tasks.values()) {
            String taskString = toString(task);
            result.add(taskString);
        }
        return result;
    }

    private String toString(AbstractTask task) {
        StringBuilder sb = new StringBuilder();
<<<<<<< HEAD
        sb.append(task.getType() + ",");
        sb.append(task.getId() + ",");
        sb.append(task.getTitle() + ",");
        sb.append(task.getDescription() + ",");
        sb.append(task.getStatus() + ";");
        if (task.getType() == Type.SUBTASK) {
            Subtask subtask = (Subtask) task;
            sb.append(subtask.getParentId());
        } else if (task.getType() == Type.EPIC) {
            EpicTask epic = (EpicTask) task;
            Map<Integer, Subtask> subtasks = epic.getSubtasks();
            for (Subtask subtask : subtasks.values()) {
                sb.append("#" + subtask.getType() + ":");
                sb.append(subtask.getId() + ":");
                sb.append(subtask.getTitle() + ":");
                sb.append(subtask.getDescription() + ":");
                sb.append(subtask.getStatus());
            }
        }
        sb.append("\n");
=======
        sb.append(task.getId() + ",");
        sb.append(task.getType() + ",");
        sb.append(task.getTitle() + ",");
        sb.append(task.getDescription() + ",");
        sb.append(task.getStatus());
        if (task.getType() == Type.SUBTASK) {
            Subtask subtask = (Subtask) task;
            sb.append(subtask.getParentId());
            sb.append("\n");
        } else if (task.getType() == Type.EPIC) {
            sb.append("\n");
            EpicTask epic = (EpicTask) task;
            Map<Integer, Subtask> subtasks = epic.getSubtasks();
            for (Subtask subtask : subtasks.values()) {
                sb.append(subtask.getId() + ",");
                sb.append(subtask.getType() + ",");
                sb.append(subtask.getTitle() + ",");
                sb.append(subtask.getDescription() + ",");
                sb.append(subtask.getStatus() + ",");
                sb.append(subtask.getParentId());
                sb.append("\n");
            }
        } else {
            sb.append("\n");
        }
>>>>>>> 13659d98c5f6edb00b3079086ec5946aa2d6eac4
        return sb.toString();
    }

    private AbstractTask fromString(String taskStringForm) {
        String[] strTask = taskStringForm.split(";");
        int id = 0;
        Task task = new Task(Type.TASK, strTask[1], strTask[2], id);
        return task;
    }

<<<<<<< HEAD
=======

>>>>>>> 13659d98c5f6edb00b3079086ec5946aa2d6eac4
    private void createSeveralTestTasksInFile() {
        // Создаём стандартную задачу
        String titleAndDescription = "Пробежка|Добежать до работы";
        Task task = fileBackedTasksManager.createStandardTask(titleAndDescription);
        System.out.print(Color.GREEN);
        System.out.println("Создана обычная задача с id = " + task.getId());
        System.out.print(Color.RESET);

        // Создаём стандартную задачу
        titleAndDescription = "Почитать сообщения в Телеграм|Открыть Телеграм и просмотреть новые сообщения";
        task = fileBackedTasksManager.createStandardTask(titleAndDescription);

        System.out.print(Color.GREEN);
        System.out.println("Создана обычная задача с id = " + task.getId());
        System.out.print(Color.RESET);

        // Создаём эпик
        titleAndDescription = "Утро рабочего дня" +
                "|Составить план рабочего дня";
        EpicTask epicTask = fileBackedTasksManager.createEpic(titleAndDescription);

        System.out.print(Color.GREEN);
        System.out.println("Создан эпик с id = " + (epicTask.getId()));
        System.out.print(Color.RESET);

        // Создаем список названий и описаний подзадач
        String[] importantTitleAndDescriptions = {"Подзадача 1|Прочитать входящие сообщения", "Подзадача 2|Получить вводные от руководителя"};
        for (String titleDescription : importantTitleAndDescriptions) {

            Subtask subtask = fileBackedTasksManager.createSubtask(titleDescription, epicTask.getId());

<<<<<<< HEAD
            epicTask = fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
=======
            //epicTask = fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
            fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
>>>>>>> 13659d98c5f6edb00b3079086ec5946aa2d6eac4
        }
        fileBackedTasksManager.addEpic(epicTask);

        // Создаём эпик
        titleAndDescription = "Прочитать рабочую корреспонденцию" +
                "|Прочитать все входящие письма и сообщения из мессенджеров";
        epicTask = fileBackedTasksManager.createEpic(titleAndDescription);

        System.out.print(Color.GREEN);
        System.out.println("Создан эпик с id = " + (epicTask.getId()));
        System.out.print(Color.RESET);

        // Создаем список названий и описаний подзадач
        String[] secondaryTitleAndDescriptions = {
                "Подзадача 1|Прочитать электронную почту",
                "Подзадача 2|Прочитать мессенджеры",
                "Подзадача 3|Прочитать соцсети"};
        for (String titleDescription : secondaryTitleAndDescriptions) {
            // Создаем подзадачу
            Subtask subtask = fileBackedTasksManager.createSubtask(titleDescription, epicTask.getId());
            // Добавляем её к эпику
<<<<<<< HEAD
            epicTask = fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
        }
        fileBackedTasksManager.addEpic(epicTask);
    }
=======
            // epicTask = fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
            fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
        }
        fileBackedTasksManager.addEpic(epicTask);
    }

    /*public Task createStandardTask(String titleAndDescription) {
        super.createStandardTask(titleAndDescription);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return
    }*/
    @Override
    public void addEpic(EpicTask epicTask) {
        super.addEpic(epicTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }
    public void addTask(Task task) {
        super.addTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void updateStandardTask(Task task, String[] newTitleAndDescription, boolean mustChangeStatus) {
        super.updateStandardTask(task, newTitleAndDescription, mustChangeStatus);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public AbstractTask findTaskByIdOrNull(int id) {
        var foundTask = super.findTaskByIdOrNull(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return foundTask;
    }
    /*@Override
    public void addSubtaskToEpic(EpicTask epicTask, Subtask subtask) {
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }*/
>>>>>>> 13659d98c5f6edb00b3079086ec5946aa2d6eac4
}
