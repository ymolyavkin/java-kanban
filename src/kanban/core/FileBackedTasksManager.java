package kanban.core;

import kanban.exceptions.ManagerSaveException;
import kanban.model.*;
import kanban.visual.Color;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path path;

    private boolean needWriteToFile;

    public FileBackedTasksManager(Path path) {
        super();
        this.path = path;
        needWriteToFile = false;
    }

    public FileBackedTasksManager() {
        // TODO: 21.03.2023
        this.path=null;
    }

    public void setNeedWriteToFile(boolean needWriteToFile) {
        this.needWriteToFile = needWriteToFile;
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

            if (multilineFromFile != null) {
                int poSeparator = multilineFromFile.indexOf(System.lineSeparator());
                multilineFromFile = multilineFromFile.substring(poSeparator + 2, multilineFromFile.length());
                int posEnd = multilineFromFile.indexOf("\r\n\r\n");
                if (posEnd != -1) {
                    String content = multilineFromFile.substring(0, posEnd);

                    tasks.addAll(Arrays.asList(content.split(System.lineSeparator())));

                    createTaskFromFile(tasks);

                    int posHystory = posEnd + 4;
                    String history = multilineFromFile.substring(posHystory, multilineFromFile.length());

                    if (!history.isEmpty()) {
                        List<Integer> idTaskFromHistory = historyFromFile(history);
                        addTasksToHistory(idTaskFromHistory);
                    }
                }
            }
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    private void addTasksToHistory(List<Integer> taskIds) {
        for (Integer id : taskIds) {
            findTaskByIdOrNull(id, true);
        }
    }

    private String historyToString(HistoryManager manager) {
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

    private List<Integer> taskIdsFromHistory(List<AbstractTask> browsingHistory) {
        List<Integer> taskIds = new ArrayList<>();

        for (AbstractTask task : browsingHistory) {
            taskIds.add(task.getId());
        }
        return taskIds;
    }

    private List<Integer> historyFromFile(String value) {
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
    protected void save() throws ManagerSaveException {
        Map<Integer, AbstractTask> standardTasks = getStandardTasks();
        Map<Integer, AbstractTask> epicTasks = getEpicTasks();
        List<String> tasksInStringForm = tasksIntoListString(standardTasks);
        List<String> epicsInStringForm = tasksIntoListString(epicTasks);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            writer.write("id, type, name, description, status, startTime, duration, epic");
            writer.newLine();
            for (String s : tasksInStringForm) {
                writer.write(s);
            }
            for (String s : epicsInStringForm) {
                writer.write(s);
            }
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime startTime = null;
            Duration duration = null;

            if (taskInfo[4] != "") {
                startTime = LocalDateTime.parse(taskInfo[4], formatter);
                //duration = Duration.parse(taskInfo[5]);
                long minutes = Long.parseLong(taskInfo[5]);
                duration = Duration.ofMinutes(minutes);
            }
            Status status = Status.valueOf(taskInfo[6]);
            Type type = Type.valueOf(taskInfo[1]);
            int id = Integer.parseInt(taskInfo[0]);
            switch (type) {
                case TASK -> {
                    restoreStandardTaskWithId(id, title, description, startTime, duration, status);
                    System.out.print(Color.GREEN);
                    System.out.println("Прочитана из файла обычная задача с id = " + id);
                    System.out.print(Color.RESET);
                }
                case EPIC -> {
                    EpicTask epicTask = restoreEpicWithId(id, title, description, status);
                    System.out.print(Color.GREEN);
                    System.out.println("Прочитана из файла эпик с id = " + id);
                    System.out.print(Color.RESET);
                    // Получаем список названий и описаний подзадач
                    for (String str : tasksFromFile) {
                        String[] taskInfoSub = str.split(",");
                        Type typeSubtask = Type.valueOf(taskInfoSub[1]);
                        int idSubtask = Integer.parseInt(taskInfoSub[0]);
                        if (typeSubtask == Type.SUBTASK) {
                            int parentId = Integer.parseInt(taskInfoSub[7]);
                            String titleSubtask = taskInfoSub[2];
                            String descriptionSubtask = taskInfoSub[3];

                            LocalDateTime startTimeSubtask = null;
                            Duration durationSubtask = null;

                            if (taskInfoSub[4] != "") {
                                startTimeSubtask = LocalDateTime.parse(taskInfoSub[4], formatter);
                                long minutes = Long.parseLong(taskInfo[5]);
                                durationSubtask = Duration.ofMinutes(minutes);
                            }

                            Subtask subtask = createSubtaskWithId(idSubtask,
                                    titleSubtask,
                                    descriptionSubtask,
                                    parentId,
                                    startTimeSubtask,
                                    durationSubtask,
                                    status);
                            if (epicTask.getId() == parentId) {
                                addSubtaskToEpic(epicTask, subtask);
                            }
                        }
                    }
                    epicTask.calculateTime();
                    addEpic(epicTask);

                }
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
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
        if (task.getStartTime() != null) {
            sb.append(task.getStartTime().format(formatter) + ",");
            long minutes = task.getDuration().toMinutes();
            //sb.append(task.getDuration() + ",");
            sb.append(minutes + ",");
        } else {
            sb.append(",");
            sb.append(",");
        }

        sb.append(task.getStatus());
        switch (typeTask) {
            case SUBTASK -> {
                Subtask subtask = (Subtask) task;
                sb.append(subtask.getParentId());
                sb.append(System.lineSeparator());
            }
            case EPIC -> {
                sb.append(System.lineSeparator());
                EpicTask epic = (EpicTask) task;

                TreeSet<Subtask> subtasks = epic.getSubtasks();
                for (Subtask subtask : subtasks) {
                    sb.append(subtask.getId() + ",");
                    sb.append(Type.SUBTASK + ",");
                    sb.append(subtask.getTitle() + ",");
                    sb.append(subtask.getDescription() + ",");
                    if (subtask.getStartTime() != null) {
                        sb.append(subtask.getStartTime().format(formatter) + ",");
                        long minutes = subtask.getDuration().toMinutes();
                        sb.append(minutes + ",");
                        //sb.append(subtask.getDuration() + ",");
                    } else {
                        sb.append(",");
                        sb.append(",");
                    }

                    sb.append(subtask.getStatus() + ",");
                    sb.append(subtask.getParentId());
                    sb.append(System.lineSeparator());
                }
            }
            default -> {
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    @Override
    public void addEpic(EpicTask epicTask) {
        epicTask.calculateTime();
        super.addEpic(epicTask);
        if (needWriteToFile) {
            save();
            needWriteToFile = false;
        }
    }

    public void addTask(Task task) {

        super.addTask(task);
        if (needWriteToFile) {
            save();
            needWriteToFile = false;
        }
    }

    @Override
    public boolean updateStandardTask(Task task, String[] newTitleAndDescription, String[] newTime, boolean mustChangeStatus) {
        boolean result = super.updateStandardTask(task, newTitleAndDescription, newTime, mustChangeStatus);
        save();
        needWriteToFile = false;
        return result;
    }

    @Override
    public AbstractTask findTaskByIdOrNull(int id, boolean savedToHistory) {
        var foundTask = super.findTaskByIdOrNull(id, savedToHistory);

        needWriteToFile = true;
        save();
        needWriteToFile = false;

        return foundTask;
    }

    @Override
    public boolean deleteTaskById(int id) {
        boolean oneTaskWasDeleted = super.deleteTaskById(id);
        save();
        needWriteToFile = false;

        return oneTaskWasDeleted;
    }

    @Override
    public boolean deleteAllTasks() {
        boolean wasDeleted = super.deleteAllTasks();
        save();
        needWriteToFile = false;

        return wasDeleted;
    }
}
