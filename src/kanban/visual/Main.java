package kanban.visual;

import kanban.core.FileBackedTasksManager;
import kanban.core.InMemoryTaskManager;
import kanban.core.Managers;
import kanban.model.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static kanban.core.InMemoryTaskManager.getInMemoryHistoryManager;

public class Main {
    private static Scanner scanner;

    static InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();
    private static final FileBackedTasksManager fileBackedTasksManager
            = new FileBackedTasksManager(Path.of("taskbacket.txt"), getInMemoryHistoryManager());

    public static void main(String[] args) {

        scanner = new Scanner(System.in);
        String userInput;

        do {
            printMenu();
            userInput = scanner.nextLine();
            switch (userInput) {
                case "1" -> getListOfAllTasks();
                case "2" -> createTask();
                case "3" -> findTaskById();
                case "4" -> updateTaskById();
                case "5" -> deleteTaskById();
                case "6" -> deleteAllTasks();
                case "7" -> createSeveralTestTasks();
                case "8" -> getBrowsingHistory();
                case "0" -> System.out.println("Выход");
                default -> System.out.println("Извините, такой команды пока нет. Введите число от 0 до 8");
            }
        } while (!userInput.equals("0"));
    }

    static void getListOfAllTasks() {
        var standardTasks = inMemoryTaskManager.getStandardTasks();
        var epicTasks = inMemoryTaskManager.getEpicTasks();
        if (standardTasks.isEmpty() && epicTasks.isEmpty()) {
            System.out.print(Color.RED);
            System.out.println("У Вас нет задач");
            System.out.print(Color.RESET);
            return;
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
    }

    static void printMenu() {
        System.out.println();
        System.out.println("1 - Получить список всех задач");
        System.out.println("2 - Создать задачу");
        System.out.println("3 - Найти задачу по идентификатору");
        System.out.println("4 - Обновить задачу");
        System.out.println("5 - Удалить задачу по идентификатору");
        System.out.println("6 - Удалить все задачи");
        System.out.println("7 - Создать несколько тестовых задач");
        System.out.println("8 - Получить историю просмотров задач");
        System.out.println("0 - Выход из программы");
    }

    static int stringToInt(String userInput) {
        // Шаблон выбирает первое число из строки
        Pattern pattern = Pattern.compile(".*?(\\d+).*");
        Matcher matcher = pattern.matcher(userInput);
        String number = "-1";
        if (matcher.find()) {
            number = matcher.group(1);
        }
        return Integer.valueOf(number);
    }

    private static int userMenu(String text, String[] menuItems, int[] values) {
        System.out.println(text + ": ");
        for (String menuItem : menuItems) {
            System.out.println(menuItem);
        }

        String userInput = scanner.nextLine();
        int menuItem = stringToInt(userInput);

        for (int value : values) {
            if (menuItem == value) return menuItem;
        }
        if (values.length > 1) {
            System.out.println("Такого варианта пока нет");
        }
        return -1;
    }

    private static String titleAndDescription() {
        System.out.println("Введите название задачи");
        String title = scanner.nextLine();
        System.out.println("Введите описание задачи");
        String description = scanner.nextLine();

        return title + "|" + description;
    }

    private static List<String> createSubtaskItemInfo() {
        System.out.println("Ввод подзадач");

        List<String> subtaskInfo = new ArrayList<>();
        int subtaskCount = 1;

        System.out.println("Для ввода " + subtaskCount + "-й подзадачи нажмите любую клавишу, '0' - закончить ввод");
        String userInput = scanner.nextLine();

        while (!userInput.equals("0")) {
            // Получаем название и описание задачи в одной строке
            String pair = titleAndDescription();

            subtaskInfo.add(pair);
            subtaskCount++;
            System.out.println("Для ввода " + subtaskCount + "-й подзадачи нажмите любую клавишу, '0' - закончить ввод");
            userInput = scanner.nextLine();
        }
        return subtaskInfo;
    }

    private static void createTask() {
        String[] menuItems = {"1 - обычная задача", "2 - эпик"};
        int[] values = {1, 2};
        int typeTask = userMenu("Выберите тип создаваемой задачи:", menuItems, values);
        int taskId;

        if (typeTask == 1) {
            Task task = fileBackedTasksManager.createStandardTask(titleAndDescription());
            System.out.print(Color.GREEN);
            System.out.println("Создана обычная задача с id = " + task.getId());
            System.out.print(Color.RESET);
        } else if (typeTask == 2) {
            EpicTask epicTask = fileBackedTasksManager.createEpic(titleAndDescription());
            System.out.print(Color.GREEN);
            System.out.println("Создан эпик с id = " + (epicTask.getId()));
            System.out.print(Color.RESET);
            // Получаем список названий и описаний подзадач
            List<String> titleAndDescriptions = createSubtaskItemInfo();
            for (String titleAndDescription : titleAndDescriptions) {
                // Создаем подзадачу
                Subtask subtask = fileBackedTasksManager.createSubtask(titleAndDescription, epicTask.getId());
                // Добавляем её к эпику
                // epicTask = fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
                fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
            }
            // Кладем эпик в мапу
            fileBackedTasksManager.addEpic(epicTask);
        }
    }

    private static void createTaskOld() {
        String[] menuItems = {"1 - обычная задача", "2 - эпик"};
        int[] values = {1, 2};
        int typeTask = userMenu("Выберите тип создаваемой задачи:", menuItems, values);
        int taskId;

        if (typeTask == 1) {
            Task task = inMemoryTaskManager.createStandardTask(titleAndDescription());
            System.out.print(Color.GREEN);
            System.out.println("Создана обычная задача с id = " + task.getId());
            System.out.print(Color.RESET);
        } else if (typeTask == 2) {
            EpicTask epicTask = inMemoryTaskManager.createEpic(titleAndDescription());
            System.out.print(Color.GREEN);
            System.out.println("Создан эпик с id = " + (epicTask.getId()));
            System.out.print(Color.RESET);
            // Получаем список названий и описаний подзадач
            List<String> titleAndDescriptions = createSubtaskItemInfo();
            for (String titleAndDescription : titleAndDescriptions) {
                // Создаем подзадачу
                Subtask subtask = inMemoryTaskManager.createSubtask(titleAndDescription, epicTask.getId());
                // Добавляем её к эпику
                // epicTask = inMemoryTaskManager.addSubtaskToEpic(epicTask, subtask);
                inMemoryTaskManager.addSubtaskToEpic(epicTask, subtask);
            }
            // Кладем эпик в мапу
            inMemoryTaskManager.addEpic(epicTask);
        }
    }

    private static void findTaskById() {
        System.out.println("Ведите id задачи");
        String stringId = scanner.nextLine();
        int id = stringToInt(stringId);
        var task = inMemoryTaskManager.findTaskByIdOrNull(id);

        if (task == null) {
            System.out.print(Color.RED);
            System.out.println("У Вас нет задач с таким id");
            System.out.print(Color.RESET);
        } else {
            //inMemoryTaskManager.addTaskIntoHistory(task);
            System.out.println(task);
        }
    }

    private static void updateTaskById() {
        System.out.println("Ведите id задачи, которую хотите обновить");
        System.out.println("Если это подзадача, то введите номер родительского эпика");

        String stringId = scanner.nextLine();
        int id = stringToInt(stringId);

        // Ищем задачу по id
        var task = inMemoryTaskManager.findTaskByIdOrNull(id);

        if (task == null || task instanceof Subtask) {
            System.out.print(Color.RED);
            System.out.println("У Вас нет задач с таким id, либо это подзадача");
            System.out.println("Для изменения подзадачи измените родительский эпик");
            System.out.print(Color.RESET);
        } else {
            // Получаем новое название и описание найденной задачи
            String[] newTitleAndDescription = updateTitleAndDescription(task.getTitle(), task.getDescription());
            // Определяем тип задачи
            if (task instanceof Task) {
                // Это обычная задача
                boolean statusWasChanged = mustChangeStatus();
                inMemoryTaskManager.updateStandardTask((Task) task, newTitleAndDescription, mustChangeStatus());
                System.out.print(Color.GREEN);

                if (statusWasChanged) {
                    System.out.println("Статус изменён");
                } else {
                    System.out.println("Статус не изменён");
                }
                System.out.print(Color.RESET);
            } else if (task instanceof EpicTask) {
                // Это эпик
                EpicTask epicTask = (EpicTask) task;
                String[] menuItems = {"'Да' - '1', 'нет' - любая клавиша"};
                int[] values = {1};

                int changeSubtasks = userMenu("Будете ли менять подзадачи данного эпика?", menuItems, values);

                if (changeSubtasks == 1) {
                    System.out.println(epicTask);
                    System.out.println("Введите id подзадачи");

                    String strSubtaskId = scanner.nextLine();
                    int subtaskId = stringToInt(strSubtaskId);

                    Map<Integer, Subtask> subtasks = epicTask.getSubtasks();

                    if (subtasks.containsKey(subtaskId)) {
                        // Обновляем подзадачу
                        Subtask subtask = subtasks.get(subtaskId);
                        // Получаем новое название и описание подзадачи
                        String[] changeTitleAndDescription
                                = updateTitleAndDescription(subtask.getTitle(), subtask.getDescription());

                        Subtask updatedSubtask = inMemoryTaskManager.updateSubtask(subtask,
                                changeTitleAndDescription, mustChangeStatus());

                        Status currentStatus = epicTask.getStatus();
                        // Добавляем обновленную подзадачу к эпику
                        //   epicTask = inMemoryTaskManager.addSubtaskToEpic(epicTask, updatedSubtask);
                        inMemoryTaskManager.addSubtaskToEpic(epicTask, updatedSubtask);
                        Status newStatus = epicTask.getStatus();

                        if (newStatus != currentStatus) {
                            System.out.print(Color.GREEN);
                            System.out.println("Статус эпика был изменён на " + newStatus);
                            System.out.print(Color.RESET);
                        }
                        // Отправляем для добавления в мапу эпиков
                        inMemoryTaskManager.addEpic(epicTask);
                    }
                }
            }
        }
    }

    private static boolean mustChangeStatus() {
        String[] menuItems = {"Для изменения статуса нажмите '1'", "Оставить прежним - нажмите любую клавишу"};
        int[] values = {1};
        int changeStatus = userMenu("Следует изменить статус задачи?", menuItems, values);

        if (changeStatus == 1) {
            return true;
        } else {
            return false;
        }
    }

    private static String[] updateTitleAndDescription(String currentTitle, String currentDescription) {
        String[] newData = new String[2];
        newData[0] = currentTitle;
        newData[1] = currentDescription;

        System.out.println("Текущее название задачи: " + currentTitle);
        System.out.println("Новое название (если ввод будет пустым, то останется старое значение):");
        String newTitle = scanner.nextLine();

        if (newTitle.equals("")) {
            System.out.println("Название не изменилось: " + currentTitle);
        } else {
            System.out.println("Новое название: " + newTitle);
            newData[0] = newTitle;
        }
        System.out.println("Текущее описание задачи: " + currentDescription);
        System.out.println("Новое описание (если ввод будет пустым, то останется старое значение):");
        String newDescription = scanner.nextLine();

        if (newDescription.equals("")) {
            System.out.println("Описание не изменилось: " + currentDescription);
        } else {
            System.out.println("Новое описание: " + newDescription);
            newData[1] = newDescription;
        }
        return newData;
    }

    private static void deleteTaskById() {
        System.out.println("Ведите id задачи");
        String stringId = scanner.nextLine();
        int id = stringToInt(stringId);
        boolean taskIsFound = inMemoryTaskManager.deleteTaskById(id);

        if (taskIsFound) {
            System.out.print(Color.GREEN);
            System.out.println("Задача удалена");
            System.out.print(Color.RESET);
        } else {
            System.out.print(Color.RED);
            System.out.println("У Вас нет задач с таким id");
            System.out.print(Color.RESET);
        }
    }

    private static void deleteAllTasks() {
        boolean taskExists = inMemoryTaskManager.deleteAllTasks();

        if (taskExists) {
            System.out.print(Color.GREEN);
            System.out.println("Все задачи удалены");
            System.out.print(Color.RESET);
        } else {
            System.out.print(Color.RED);
            System.out.println("У Вас нет задач");
            System.out.print(Color.RESET);
        }
    }

    private static void createSeveralTestTasksOld() {
        // Создаём стандартную задачу
        String titleAndDescription = "Физминутка|Выполнить десять приседаний";
        Task task = inMemoryTaskManager.createStandardTask(titleAndDescription);
        System.out.print(Color.GREEN);
        System.out.println("Создана обычная задача с id = " + task.getId());
        System.out.print(Color.RESET);

        // Создаём стандартную задачу
        titleAndDescription = "Почитать новости|Открыть мессенджер и просмотреть новые сообщения";
        task = inMemoryTaskManager.createStandardTask(titleAndDescription);

        System.out.print(Color.GREEN);
        System.out.println("Создана обычная задача с id = " + task.getId());
        System.out.print(Color.RESET);

        // Создаём эпик
        titleAndDescription = "Понять условие домашнего задания" +
                "|Понять как сделать рефакторинг проекта 'Трекер задач' в соответствии с новым ТЗ";
        EpicTask epicTask = inMemoryTaskManager.createEpic(titleAndDescription);

        System.out.print(Color.GREEN);
        System.out.println("Создан эпик с id = " + (epicTask.getId()));
        System.out.print(Color.RESET);

        // Создаем список названий и описаний подзадач
        String[] importantTitleAndDescriptions = {"Подзадача 1|Прочитать ТЗ", "Подзадача 2|Понять ТЗ"};
        for (String titleDescription : importantTitleAndDescriptions) {

            Subtask subtask = inMemoryTaskManager.createSubtask(titleDescription, epicTask.getId());

            // epicTask = inMemoryTaskManager.addSubtaskToEpic(epicTask, subtask);
            inMemoryTaskManager.addSubtaskToEpic(epicTask, subtask);
        }
        inMemoryTaskManager.addEpic(epicTask);

        // Создаём эпик
        titleAndDescription = "Прочитать почту" +
                "|Прочитать все входящие письма и сообщения из мессенджеров";
        epicTask = inMemoryTaskManager.createEpic(titleAndDescription);

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
            Subtask subtask = inMemoryTaskManager.createSubtask(titleDescription, epicTask.getId());
            // Добавляем её к эпику
            //  epicTask = inMemoryTaskManager.addSubtaskToEpic(epicTask, subtask);
            inMemoryTaskManager.addSubtaskToEpic(epicTask, subtask);
        }
        inMemoryTaskManager.addEpic(epicTask);
    }

    private static void createSeveralTestTasks() {
        // Создаём стандартную задачу
        String titleAndDescription = "Физминутка|Выполнить десять приседаний";
        Task task = fileBackedTasksManager.createStandardTask(titleAndDescription);
        System.out.print(Color.GREEN);
        System.out.println("Создана обычная задача с id = " + task.getId());
        System.out.print(Color.RESET);

        // Создаём стандартную задачу
        titleAndDescription = "Почитать новости|Открыть мессенджер и просмотреть новые сообщения";
        task = fileBackedTasksManager.createStandardTask(titleAndDescription);

        System.out.print(Color.GREEN);
        System.out.println("Создана обычная задача с id = " + task.getId());
        System.out.print(Color.RESET);

        // Создаём эпик
        titleAndDescription = "Понять условие домашнего задания" +
                "|Понять как сделать рефакторинг проекта 'Трекер задач' в соответствии с новым ТЗ";
        EpicTask epicTask = fileBackedTasksManager.createEpic(titleAndDescription);

        System.out.print(Color.GREEN);
        System.out.println("Создан эпик с id = " + (epicTask.getId()));
        System.out.print(Color.RESET);

        // Создаем список названий и описаний подзадач
        String[] importantTitleAndDescriptions = {"Подзадача 1|Прочитать ТЗ", "Подзадача 2|Понять ТЗ"};
        for (String titleDescription : importantTitleAndDescriptions) {

            Subtask subtask = fileBackedTasksManager.createSubtask(titleDescription, epicTask.getId());

            // epicTask = fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
            fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
        }
        fileBackedTasksManager.addEpic(epicTask);

        // Создаём эпик
        titleAndDescription = "Прочитать почту" +
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
            //  epicTask = fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
            fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
        }
        fileBackedTasksManager.addEpic(epicTask);
    }

    private static void getBrowsingHistory() {
        List<AbstractTask> historyFindTask = inMemoryTaskManager.getHistory();
        if (historyFindTask.isEmpty()) {
            System.out.print(Color.RED);
            System.out.println("История просмотров пуста");
            System.out.print(Color.RESET);

            return;
        }
        int count = 1;
        for (AbstractTask abstractTask : historyFindTask) {
            System.out.print(count + " задача: ");
            System.out.println(abstractTask);
            count++;
            System.out.println("-------------------------------------------------------------------------------------");
        }
    }
}


