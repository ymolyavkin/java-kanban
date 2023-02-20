package kanban.core;

import kanban.exceptions.ManagerSaveException;
import kanban.model.*;
import kanban.visual.Color;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path path;


    public FileBackedTasksManager(Path path) {
        super();
        this.path = path;
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        FileBackedTasksManager fileBackedTasksManager = Managers.getFileBackedTasksManager(path);
        fileBackedTasksManager.restoreDataFromFile();

        return fileBackedTasksManager;
    }

    /**
     * все методы, кроме loadFromFile должны быть не статическими
     */
    private void restoreDataFromFile() {
        List<String> tasks = new ArrayList<>();
        try {
            String multilineFromFile = readFileOrNull();
            int poSeparator= multilineFromFile.indexOf(System.lineSeparator());
            System.out.println(poSeparator);
            if (multilineFromFile != null) {
                int startPosition = multilineFromFile.indexOf("\n") + 1;
                int endPosition = multilineFromFile.indexOf("\n\n");
                String content = multilineFromFile.substring(startPosition, endPosition);

                tasks.addAll(Arrays.asList(content.split("\n")));
                createTaskFromFile(tasks);

                String history = multilineFromFile.substring(endPosition, multilineFromFile.length());

                if (!history.isEmpty()) {
                    int posNewLine = history.indexOf("\n\n");
                    int nextPosNewLine = history.indexOf("\n", posNewLine + 1);

                    history = history.substring(posNewLine + 2);

                    List<Integer> idTaskFromHistory = historyFromFile(history);
                    addTasksToHistory(idTaskFromHistory);
                }
            }
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    private void addTasksToHistory(List<Integer> taskIds) {
        for (Integer id : taskIds) {
            findTaskByIdOrNull(id);
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

    private static List<Integer> historyFromFile(String value) {
        List<Integer> history = new ArrayList<>();
        if (!value.isEmpty()) {
            String[] ids = value.split(",");
            for (String id : ids) {
                history.add(Integer.parseInt(id));
            }
        }
        return history;
    }


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
            writer.write("id, type, name, description, status, epic");
            writer.newLine();
            for (String s : tasksInStringForm) {
                writer.write(s);
            }
            for (String s : epicsInStringForm) {
                writer.write(s);
            }
           // writer.write("\n");
            writer.newLine();
            writer.write(historyToString(getInMemoryHistoryManager()));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }

    }

    public String readFileOrNull() throws ManagerSaveException {
        String content = null;
        try {
            if (Files.exists(path)) {
                content = Files.readString(path);
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        return content;
    }

    private void createTaskFromFile(List<String> tasksFromFile) {
        for (String s : tasksFromFile) {
            if (s.substring(0, 2).equals("id")) {
                continue;
            }
            String[] taskInfo = s.split(",");
            String title = taskInfo[2];
            String description = taskInfo[3];
            Type type = Type.valueOf(taskInfo[1]);
            int id = Integer.parseInt(taskInfo[0]);
            switch (type) {
                case TASK -> {
                    createStandardTaskWithId(id, title, description);
                    System.out.print(Color.GREEN);
                    System.out.println("Прочитана из файла обычная задача с id = " + id);
                    System.out.print(Color.RESET);
                }
                case EPIC -> {
                    EpicTask epicTask = createEpicWithId(id, title, description);
                    System.out.print(Color.GREEN);
                    System.out.println("Прочитана из файла эпик с id = " + id);
                    System.out.print(Color.RESET);
                    // Получаем список названий и описаний подзадач
                    for (String str : tasksFromFile) {
                        String[] taskInfoSub = str.split(",");
                        Type typeSubtask = Type.valueOf(taskInfoSub[1]);
                        int idSubtask = Integer.parseInt(taskInfoSub[0]);
                        if (typeSubtask == Type.SUBTASK) {
                            int parentId = Integer.parseInt(taskInfoSub[5]);
                            String titleSubtask = taskInfoSub[2];
                            String descriptionSubtask = taskInfoSub[3];
                            Subtask subtask = createSubtaskWithId(idSubtask
                                    , titleSubtask
                                    , descriptionSubtask
                                    , parentId);
                            if (epicTask.getId() == parentId) {
                                addSubtaskToEpic(epicTask, subtask);
                            }
                        }
                    }
                    addEpic(epicTask);
                }
            }
            /*if (typeTask.equals("TASK")) {
                Task task = createStandardTaskWithId(id, title, description);
                System.out.print(Color.GREEN);
                System.out.println("Прочитана из файла обычная задача с id = " + id);
                System.out.print(Color.RESET);
            } else if (typeTask.equals("EPIC")) {
                EpicTask epicTask = createEpicWithId(id, title, description);
                System.out.print(Color.GREEN);
                System.out.println("Прочитана из файла эпик с id = " + id);
                System.out.print(Color.RESET);
                // Получаем список названий и описаний подзадач
                for (String str : tasksFromFile) {
                    String[] taskInfoSub = str.split(",");
                    String typeSubtask = taskInfoSub[1];
                    int idSubtask = Integer.parseInt(taskInfoSub[0]);
                    if (typeSubtask.equals("SUBTASK")) {
                        int parentId = Integer.parseInt(taskInfoSub[5]);
                        String titleSubtask = taskInfoSub[2];
                        String descriptionSubtask = taskInfoSub[3];
                        Subtask subtask = createSubtaskWithId(idSubtask
                                , titleSubtask
                                , descriptionSubtask
                                , parentId);
                        if (epicTask.getId() == parentId) {
                            addSubtaskToEpic(epicTask, subtask);
                        }
                    }
                }
                addEpic(epicTask);
            }*/
        }

    }

    private List<String> tasksIntoListString(Map<Integer, AbstractTask> tasks) {
        List<String> result = new ArrayList<>();
        for (AbstractTask task : tasks.values()) {
            String taskString = toString(task);
            result.add(taskString);
        }
        return result;
    }

    /**
     * Здесь отказался от использования StringJoiner, т.к. мне показалось, что код получился более громоздким,
     * по причине того, что разделитель нужно ставить не везде.
     *
     *
     */
    private String toString(AbstractTask task) {
        //StringJoiner sj = new StringJoiner(",");
        StringBuilder sb = new StringBuilder();
        Type typeTask;
        if (task instanceof Task) {
            typeTask = Type.TASK;
        } else if (task instanceof EpicTask) {
            typeTask = Type.EPIC;
        } else {
            typeTask = Type.SUBTASK;
        }
        sb.append(task.getId() + ",");
        sb.append(typeTask + ",");
        sb.append(task.getTitle() + ",");
        sb.append(task.getDescription() + ",");
        sb.append(task.getStatus());
        switch (typeTask) {
            case SUBTASK -> {
                Subtask subtask = (Subtask) task;
                sb.append(subtask.getParentId());
                sb.append("\n");
            }
            case EPIC -> {
                sb.append("\n");
                EpicTask epic = (EpicTask) task;
                Map<Integer, Subtask> subtasks = epic.getSubtasks();
                for (Subtask subtask : subtasks.values()) {
                    sb.append(subtask.getId() + ",");
                    sb.append(Type.SUBTASK + ",");
                    sb.append(subtask.getTitle() + ",");
                    sb.append(subtask.getDescription() + ",");
                    sb.append(subtask.getStatus() + ",");
                    sb.append(subtask.getParentId());
                    sb.append("\n");
                }
            }
            default -> {
                sb.append("\n");
            }
        }
        /*if (typeTask == Type.SUBTASK) {
            Subtask subtask = (Subtask) task;
            sb.append(subtask.getParentId());
            sb.append("\n");
        } else if (typeTask == Type.EPIC) {
            sb.append("\n");
            EpicTask epic = (EpicTask) task;
            Map<Integer, Subtask> subtasks = epic.getSubtasks();
            for (Subtask subtask : subtasks.values()) {
                sb.append(subtask.getId() + ",");
                sb.append(Type.SUBTASK + ",");
                sb.append(subtask.getTitle() + ",");
                sb.append(subtask.getDescription() + ",");
                sb.append(subtask.getStatus() + ",");
                sb.append(subtask.getParentId());
                sb.append("\n");
            }
        } else {
            sb.append("\n");
        }*/
        return sb.toString();
    }

    private AbstractTask fromString(String taskStringForm) {
        String[] strTask = taskStringForm.split(";");
        int id = 0;
        Task task = new Task(strTask[1], strTask[2], id);
        return task;
    }

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

    @Override
    public boolean deleteTaskById(int id) {
        boolean oneTaskWasDeleted = super.deleteTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return oneTaskWasDeleted;
    }

    @Override
    public boolean deleteAllTasks() {
        boolean wasDeleted = super.deleteAllTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return wasDeleted;
    }
}
