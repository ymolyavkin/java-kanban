package kanban.core;

import kanban.model.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public class InFileHistoryManager implements HistoryManager {

    /**
     * @param task
     */
    @Override
    public void add(AbstractTask task) {
        StringBuilder sb = new StringBuilder();
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
        String result= sb.toString();
        System.out.println(result);
        try {
            save2(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @param id
     */
    @Override
    public void remove(int id) {

    }

    /**
     * @return
     */
    @Override
    public List<AbstractTask> getHistory() {
        return null;
    }

    private void save(String taskInfo) {
        String filename = "taskfile.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, Charset.forName("UTF-8")))) {

            writer.write(taskInfo + "\n");

        } catch (IOException e) {
            // handle the exception
            System.out.println("Произошла ошибка во время записи файла.");
        }
    }

    private void save2(String content) throws IOException {
        Path path = Path.of("taskfile.txt");
        Files.writeString(path, content + "\n", StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    /*private void writeListToFile(List<String> list, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, Charset.forName("UTF-8")))) {
            for (String str : list) {
                writer.write(str + "\n");
            }
        } catch (IOException e) {
            // handle the exception
            System.out.println("Произошла ошибка во время записи файла.");
        }
    }*/
}
