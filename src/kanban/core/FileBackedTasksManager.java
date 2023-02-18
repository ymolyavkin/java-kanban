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


    private FileBackedTasksManager(Path path, HistoryManager historyManager) {
        super();
        this.path = path;
        this.historyManager = historyManager;
    }

    private static void restoreDataFromFile() {
        List<String> tasks = new ArrayList<>();
        try {
            String multilineFromFile = fileBackedTasksManager.readFileOrNull();
            if (multilineFromFile != null) {
                int startPosition = multilineFromFile.indexOf("\n") + 1;
                int endPosition = multilineFromFile.indexOf("\n\n");
                String content = multilineFromFile.substring(startPosition, endPosition);

                tasks.addAll(Arrays.asList(content.split("\n")));
                fileBackedTasksManager.createTaskFromFile(tasks);

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

    private static void addTasksToHistory(List<Integer> taskIds) {
        for (Integer id : taskIds) {
            fileBackedTasksManager.findTaskByIdOrNull(id);
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
        String[] ids = value.split(",");
        for (String id : ids) {
            history.add(Integer.parseInt(id));
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
            if (Files.exists(path)) {
                content = Files.readString(path);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки файла");
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
                    String typeSubtask = taskInfoSub[1];
                    int idSubtask = Integer.parseInt(taskInfoSub[0]);
                    if (typeSubtask.equals("SUBTASK")) {
                        int parentId = Integer.parseInt(taskInfoSub[5]);
                        String titleSubtask = taskInfoSub[2];
                        String descriptionSubtask = taskInfoSub[3];
                        Subtask subtask = fileBackedTasksManager.createSubtaskWithId(idSubtask
                                , titleSubtask
                                , descriptionSubtask
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
