package kanban.core;

import kanban.exceptions.ManagerSaveException;
import kanban.model.*;
import kanban.visual.Color;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path path;
    private static HistoryManager historyManager;
    private static FileBackedTasksManager fileBackedTasksManager;

    public static FileBackedTasksManager getFileBackedTasksManager() {
        fileBackedTasksManager
                = new FileBackedTasksManager(Path.of("taskbacket.txt"), getInMemoryHistoryManager());
        return fileBackedTasksManager;
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        fileBackedTasksManager
                = new FileBackedTasksManager(path, getInMemoryHistoryManager());
        restoreDataFromFile();

        return fileBackedTasksManager;
    }
   /* private static final FileBackedTasksManager fileBackedTasksManager
            = new FileBackedTasksManager(Path.of("taskbacket.txt"), getInMemoryHistoryManager());*/

    private FileBackedTasksManager(Path path, HistoryManager historyManager) {
        super();
        this.path = path;
        this.historyManager = historyManager;
    }

/*    public static void main(String[] args) throws ManagerSaveException {

        System.out.println("From file backed manager");
        //FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Path.of("taskbacket.txt"));
        fileBackedTasksManager.createSeveralTestTasksInFile();
        fileBackedTasksManager.findTaskByIdOrNull(0);
        fileBackedTasksManager.findTaskByIdOrNull(1);


        try {
            fileBackedTasksManager.save();
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }*/

    private static void restoreDataFromFile() {
        List<String> tasks  = new ArrayList<>();
        try {
            String multilineFromFile = fileBackedTasksManager.readFileOrNull();
            tasks.addAll(Arrays.asList(multilineFromFile.split("\n")));
            fileBackedTasksManager.createTaskFromFile(tasks);
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

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
            writer.write("id, type, name, description, status, epic \n");
            for (String s : tasksInStringForm) {
                writer.write(s);
            }
            for (String s : epicsInStringForm) {
                writer.write(s);
            }
            writer.write("\n");
            writer.write(historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }

    }

    public String readFileOrNull() throws ManagerSaveException {
        String content = null;
        try {
            content = Files.readString(path);
            System.out.println(content);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки файла");
        }
        return content;
    }

    private void createTaskFromFile(List<String> tasksFromFile) {
        for (String s : tasksFromFile) {
            if (s.substring(0,2).equals("id")) {
                continue;
            }
            String[] taskInfo = s.split(",");
            String title = taskInfo[2];
            String description = taskInfo[3];
            String typeTask = taskInfo[1];
            int id = Integer.parseInt(taskInfo[0]);
            if (typeTask.equals("TASK")) {
                Task task = fileBackedTasksManager.createStandardTaskWithId(id, title, description);
                System.out.print(Color.GREEN);
                System.out.println("Прочитана из файла обычная задача с id = " + id);
                System.out.print(Color.RESET);
            } else if (typeTask.equals("EPIC")) {
                EpicTask epicTask = fileBackedTasksManager.createEpicWithId(id, title, description);
                System.out.print(Color.GREEN);
                System.out.println("Прочитана из файла эпик с id = " + id);
                System.out.print(Color.RESET);
                // Получаем список названий и описаний подзадач
                for (String str : tasksFromFile) {
                    String[] taskInfoSub = str.split(",");
                    typeTask = taskInfoSub[1];
                    int idSubtask = Integer.parseInt(taskInfoSub[0]);
                    if (typeTask.equals("SUBTASK")) {
                        int parentId = Integer.parseInt(taskInfoSub[5]);
                        title = taskInfo[2];
                        description = taskInfo[3];
                        Subtask subtask = fileBackedTasksManager.createSubtaskWithId(idSubtask
                                , title
                                , description
                                , parentId);
                        if (epicTask.getId() == parentId) {
                            addSubtaskToEpic(epicTask, subtask);
                        }
                    }
                }
                fileBackedTasksManager.addEpic(epicTask);
            }
        }

    }
    /*private String taskIdsFromHistoryInOneString(List<Integer> taskIds) {
        StringBuilder sb = new StringBuilder();
        for (Integer id : taskIds) {
            sb.append(String.valueOf(id));
        }
        return sb.toString();
    }*/

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
        return sb.toString();
    }

    private AbstractTask fromString(String taskStringForm) {
        String[] strTask = taskStringForm.split(";");
        int id = 0;
        Task task = new Task(Type.TASK, strTask[1], strTask[2], id);
        return task;
    }


   /* private void createSeveralTestTasksInFile() {
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

            //epicTask = fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
            fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
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
            // epicTask = fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
            fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
        }
        fileBackedTasksManager.addEpic(epicTask);
    }
*/
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
}
