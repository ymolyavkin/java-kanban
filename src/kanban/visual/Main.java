package kanban.visual;

import com.sun.net.httpserver.HttpServer;
import kanban.core.FileBackedTasksManager;
import kanban.core.HttpTaskManager;
import kanban.core.Managers;
import kanban.model.*;
import kanban.tasksAPI.HttpTaskServer;
import kanban.tasksAPI.KVServer;


import java.io.IOException;
import java.net.InetSocketAddress;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    private static Scanner scanner;

    // static InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();

    private static final FileBackedTasksManager fileBackedTasksManager
            = FileBackedTasksManager.loadFromFile(Path.of("taskbacket.txt"));

   // private static final HttpTaskManager httpTaskManager = (HttpTaskManager) Managers.getDefault();
    private static HttpTaskManager httpTaskManager;
    public static final int KV_PORT = 8078;
    public static final int TASK_PORT = 8080;

    public static void main(String[] args) throws IOException {

        new KVServer().start();
        System.out.println("HTTP-KV-сервер запущен на " + KV_PORT + " порту!");

        httpTaskManager = (HttpTaskManager) Managers.getDefault();

        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(TASK_PORT), 0);
        //httpServer.createContext("/tasks", new HttpTaskServer(key));
        httpServer.createContext("/tasks", new HttpTaskServer(httpTaskManager));
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + TASK_PORT + " порту!");

       // httpTaskManager = (HttpTaskManager) Managers.getDefault();

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
                case "9" -> getProritizedTask();
                case "0" -> System.out.println("Выход");
                default -> System.out.println("Извините, такой команды пока нет. Введите число от 0 до 8");
            }
        } while (!userInput.equals("0"));
    }

    static void getProritizedTask() {
        TreeSet<AbstractTask> myTasks = httpTaskManager.getPrioritizedTasks();
       // TreeSet<AbstractTask> myTasks = fileBackedTasksManager.getPrioritizedTasks();
        if (myTasks.isEmpty()) {
            System.out.print(Color.RED);
            System.out.println("У Вас нет задач");
            System.out.print(Color.RESET);
            return;
        }
        for (AbstractTask task : myTasks) {
            System.out.println(task);
        }
    }

    static void getListOfAllTasks() {
        httpTaskManager.restoreDataFromServer();
        var standardTasks = fileBackedTasksManager.getStandardTasks();
        var epicTasks = fileBackedTasksManager.getEpicTasks();
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
        System.out.println("9 - Получить отсортированный по времени начала список всех задач");
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

        System.out.println("Введите время начала задачи в формате dd.MM.yyyy HH:mm ");
        String stringStartTime = scanner.nextLine();
        System.out.println("Введите продолжительность задачи в минутах: ");
        String stringDuration = scanner.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        try {
            LocalDateTime startTime = LocalDateTime.parse(stringStartTime, formatter);
            //long duration = Long.parseLong(stringDuration);
        } catch (DateTimeParseException exception) {
            System.out.println("Некорректный ввод. Время не введено");
            stringStartTime = "0";
        }
        try {
            long duration = Long.parseLong(stringDuration);
        } catch (NumberFormatException exception) {
            stringDuration = "0";
        }

        return title + "|" + description + "|" + stringStartTime + "|" + stringDuration;
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

        if (typeTask == 1) {
            fileBackedTasksManager.setNeedWriteToFile(true);
            Task task = fileBackedTasksManager.createStandardTask(titleAndDescription());
            if (task != null) {
                System.out.print(Color.GREEN);
                System.out.println("Создана обычная задача с id = " + task.getId());
                System.out.print(Color.RESET);
            } else {
                System.out.print(Color.RED);
                System.out.println("Задача не создана");
                System.out.print(Color.RESET);
            }
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
                if (subtask != null) {
                    fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
                } else {
                    System.out.print(Color.RED);
                    System.out.println("Подзадача не создана");
                    System.out.print(Color.RESET);
                }

            }
            fileBackedTasksManager.setNeedWriteToFile(true);
            fileBackedTasksManager.addEpic(epicTask);
        }
    }

    private static void findTaskById() {
        System.out.println("Ведите id задачи");
        String stringId = scanner.nextLine();
        int id = stringToInt(stringId);
        var task = fileBackedTasksManager.findTaskByIdOrNull(id);

        if (task == null) {
            System.out.print(Color.RED);
            System.out.println("У Вас нет задач с таким id");
            System.out.print(Color.RESET);
        } else {
            System.out.println(task);
        }
    }

    private static void updateTaskById() {
        System.out.println("Ведите id задачи, которую хотите обновить");
        System.out.println("Если это подзадача, то введите номер родительского эпика");

        String stringId = scanner.nextLine();
        int id = stringToInt(stringId);

        // Ищем задачу по id
        var task = fileBackedTasksManager.findTaskByIdOrNull(id);

        if (task == null || task instanceof Subtask) {
            System.out.print(Color.RED);
            System.out.println("У Вас нет задач с таким id, либо это подзадача");
            System.out.println("Для изменения подзадачи измените родительский эпик");
            System.out.print(Color.RESET);
        } else {
            // Получаем новое название и описание найденной задачи
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            String startTime = "";
            String duration = "";
            if (task.getStartTime() != null) {
                startTime = task.getStartTime().format(formatter);
                duration = String.valueOf(task.getDuration().toMinutes());
            }
            String[] newTitleAndDescription =
                    updateTitleAndDescription(task.getTitle(), task.getDescription());
            // Определяем тип задачи
            if (task instanceof Task) {
                System.out.println("Это обычная задача");
                String[] newTime = updateTime(startTime, duration);
                boolean statusWasChanged = mustChangeStatus();
                //boolean taskIsUpdated = fileBackedTasksManager.updateStandardTask((Task) task, newTitleAndDescription, newTime, mustChangeStatus());
                boolean taskIsUpdated = fileBackedTasksManager.updateStandardTask((Task) task, newTitleAndDescription, newTime, statusWasChanged);
                if (taskIsUpdated) {
                    System.out.print(Color.GREEN);

                    if (statusWasChanged) {
                        System.out.println("Статус изменён");
                    } else {
                        System.out.println("Статус не изменён");
                    }
                    System.out.print(Color.RESET);
                } else {
                    System.out.print(Color.RED);
                    System.out.println("Задача не обновлена");
                    System.out.print(Color.RESET);
                }
            } else if (task instanceof EpicTask) {
                System.out.println("Это эпик");
                EpicTask epicTask = (EpicTask) task;
                fileBackedTasksManager.updateEpic((EpicTask) task, newTitleAndDescription);

                String[] menuItems = {"'Да' - '1', 'нет' - любая клавиша"};
                int[] values = {1};

                int changeSubtasks = userMenu("Будете ли менять подзадачи данного эпика?", menuItems, values);

                if (changeSubtasks == 1) {
                    System.out.println(epicTask);
                    System.out.println("Введите id подзадачи");

                    String strSubtaskId = scanner.nextLine();
                    int subtaskId = stringToInt(strSubtaskId);

                    TreeSet<Subtask> subtasks = epicTask.getSubtasks();
                    Subtask subtask = fileBackedTasksManager.findSubtaskByIdOrNull(subtaskId, subtasks);
                    if (subtask != null) {
                        String currentStartTime = subtask.getStartTime().format(formatter);
                        String currentDuration = String.valueOf(subtask.getDuration().toMinutes());
                        String[] changeTitleAndDescription = updateTitleAndDescription(subtask.getTitle(), subtask.getDescription());

                        String[] changeTime = updateTime(currentStartTime, currentDuration);
                        Subtask updatedSubtask = fileBackedTasksManager.updateSubtask(subtask,
                                changeTitleAndDescription, changeTime, mustChangeStatus());

                        Status currentStatus = epicTask.getStatus();
                        // Добавляем обновленную подзадачу к эпику
                        if (updatedSubtask != null) {
                            fileBackedTasksManager.addSubtaskToEpic(epicTask, updatedSubtask);
                            Status newStatus = epicTask.getStatus();

                            if (newStatus != currentStatus) {
                                System.out.print(Color.GREEN);
                                System.out.println("Статус эпика был изменён на " + newStatus);
                                System.out.print(Color.RESET);
                            } else {
                                System.out.print(Color.RED);
                                System.out.println("Подзадача не обновлена");
                                System.out.print(Color.RESET);
                            }
                        }

                        // Отправляем для добавления в мапу эпиков
                        fileBackedTasksManager.setNeedWriteToFile(true);
                        fileBackedTasksManager.addEpic(epicTask);
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

    private static String[] updateTime(String currentStartTime, String currentDuration) {
        String[] newData = new String[2];
        newData[0] = currentStartTime;
        newData[1] = currentDuration;

        System.out.println("Текущее время начала задачи: " + currentStartTime);
        System.out.println("Новое время (если ввод будет пустым, то останется старое значение):");
        String newStartTime = scanner.nextLine();

        if (newStartTime.equals("")) {
            System.out.println("Время не изменилось: " + currentStartTime);
        } else {
            System.out.println("Новое время: " + newStartTime);
            newData[0] = newStartTime;
        }
        System.out.println("Текущая продолжительность задачи: " + currentDuration);
        System.out.println("Новая продолжительность (если ввод будет пустым, то останется старое значение):");
        String newDuration = scanner.nextLine();

        if (newDuration.equals("")) {
            System.out.println("Продолжительность не изменилось: " + currentDuration);
        } else {
            System.out.println("Новая продолжительность: " + newDuration);
            newData[1] = newDuration;
        }
        return newData;
    }

    private static void deleteTaskById() {
        System.out.println("Ведите id задачи");
        String stringId = scanner.nextLine();
        int id = stringToInt(stringId);
        boolean taskIsFound = fileBackedTasksManager.deleteTaskById(id);

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
        boolean taskExists = fileBackedTasksManager.deleteAllTasks();

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
    private static void createSeveralTestTasks() {
        // Создаём стандартную задачу
        String titleAndDescription = "Физминутка|Выполнить десять приседаний|23.02.2023 12:24|15";
        Task task = httpTaskManager.createStandardTask(titleAndDescription);
        if (task != null) {
            System.out.print(Color.GREEN);
            System.out.println("Создана обычная задача с id = " + task.getId());
            System.out.print(Color.RESET);
        } else {
            System.out.print(Color.RED);
            System.out.println("Задача не создана");
            System.out.print(Color.RESET);
        }

        // Создаём стандартную задачу
        titleAndDescription = "Почитать новости|Открыть мессенджер и просмотреть новые сообщения|24.02.2023 12:24|15";
        task = httpTaskManager.createStandardTask(titleAndDescription);

        if (task != null) {
            System.out.print(Color.GREEN);
            System.out.println("Создана обычная задача с id = " + task.getId());
            System.out.print(Color.RESET);
        } else {
            System.out.print(Color.RED);
            System.out.println("Задача не создана");
            System.out.print(Color.RESET);
        }

        // Создаём эпик
        titleAndDescription = "Понять условие домашнего задания" +
                "|Понять как сделать рефакторинг проекта 'Трекер задач' в соответствии с новым ТЗ|25.02.2023 12:24|30";
        EpicTask epicTask = httpTaskManager.createEpic(titleAndDescription);

        System.out.print(Color.GREEN);
        System.out.println("Создан эпик с id = " + (epicTask.getId()));
        System.out.print(Color.RESET);


        String[] importantTitleAndDescriptions = {
                "Подзадача 1|Прочитать ТЗ|26.02.2023 12:20|15"
                , "Подзадача 2|Понять ТЗ|27.02.2023 12:39|15"};
        for (String titleDescription : importantTitleAndDescriptions) {

            Subtask subtask = httpTaskManager.createSubtask(titleDescription, epicTask.getId());
            if (subtask != null) {
                httpTaskManager.addSubtaskToEpic(epicTask, subtask);
                System.out.print(Color.GREEN);
                System.out.println("Создана подзадача с id = " + subtask.getId());
                System.out.print(Color.RESET);
            } else {
                System.out.print(Color.RED);
                System.out.println("Подзадача не создана");
                System.out.print(Color.RESET);
            }
        }
        httpTaskManager.setNeedWriteToFile(true);
        httpTaskManager.addEpic(epicTask);

        // Создаём эпик
        titleAndDescription = "Прочитать почту" +
                "|Прочитать все входящие письма и сообщения из мессенджеров|28.02.2023 12:24|45";
        epicTask = httpTaskManager.createEpic(titleAndDescription);

        System.out.print(Color.GREEN);
        System.out.println("Создан эпик с id = " + (epicTask.getId()));
        System.out.print(Color.RESET);

        // Создаем список названий и описаний подзадач
        String[] secondaryTitleAndDescriptions = {
                "Подзадача 1|Прочитать электронную почту|21.02.2023 12:24|15",
                "Подзадача 2|Прочитать мессенджеры|22.02.2023 12:39|15",
                "Подзадача 3|Прочитать соцсети|22.02.2023 12:59|15"};
        for (String titleDescription : secondaryTitleAndDescriptions) {
            // Создаем подзадачу
            Subtask subtask = httpTaskManager.createSubtask(titleDescription, epicTask.getId());
            // Добавляем её к эпику

            if (subtask != null) {
                httpTaskManager.addSubtaskToEpic(epicTask, subtask);
                System.out.print(Color.GREEN);
                System.out.println("Создана подзадача с id = " + subtask.getId());
                System.out.print(Color.RESET);
            } else {
                System.out.print(Color.RED);
                System.out.println("Подзадача не создана");
                System.out.print(Color.RESET);
            }
        }
        httpTaskManager.setNeedWriteToFile(true);
        httpTaskManager.addEpic(epicTask);
    }
    private static void createSeveralTestTasksOld() {
        // Создаём стандартную задачу
        String titleAndDescription = "Физминутка|Выполнить десять приседаний|23.02.2023 12:24|15";
        Task task = fileBackedTasksManager.createStandardTask(titleAndDescription);
        if (task != null) {
            System.out.print(Color.GREEN);
            System.out.println("Создана обычная задача с id = " + task.getId());
            System.out.print(Color.RESET);
        } else {
            System.out.print(Color.RED);
            System.out.println("Задача не создана");
            System.out.print(Color.RESET);
        }

        // Создаём стандартную задачу
        titleAndDescription = "Почитать новости|Открыть мессенджер и просмотреть новые сообщения|24.02.2023 12:24|15";
        task = fileBackedTasksManager.createStandardTask(titleAndDescription);

        if (task != null) {
            System.out.print(Color.GREEN);
            System.out.println("Создана обычная задача с id = " + task.getId());
            System.out.print(Color.RESET);
        } else {
            System.out.print(Color.RED);
            System.out.println("Задача не создана");
            System.out.print(Color.RESET);
        }

        // Создаём эпик
        titleAndDescription = "Понять условие домашнего задания" +
                "|Понять как сделать рефакторинг проекта 'Трекер задач' в соответствии с новым ТЗ|25.02.2023 12:24|30";
        EpicTask epicTask = fileBackedTasksManager.createEpic(titleAndDescription);

        System.out.print(Color.GREEN);
        System.out.println("Создан эпик с id = " + (epicTask.getId()));
        System.out.print(Color.RESET);


        String[] importantTitleAndDescriptions = {
                "Подзадача 1|Прочитать ТЗ|26.02.2023 12:20|15"
                , "Подзадача 2|Понять ТЗ|27.02.2023 12:39|15"};
        for (String titleDescription : importantTitleAndDescriptions) {

            Subtask subtask = fileBackedTasksManager.createSubtask(titleDescription, epicTask.getId());
            if (subtask != null) {
                fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
                System.out.print(Color.GREEN);
                System.out.println("Создана подзадача с id = " + subtask.getId());
                System.out.print(Color.RESET);
            } else {
                System.out.print(Color.RED);
                System.out.println("Подзадача не создана");
                System.out.print(Color.RESET);
            }
        }
        fileBackedTasksManager.setNeedWriteToFile(true);
        fileBackedTasksManager.addEpic(epicTask);

        // Создаём эпик
        titleAndDescription = "Прочитать почту" +
                "|Прочитать все входящие письма и сообщения из мессенджеров|28.02.2023 12:24|45";
        epicTask = fileBackedTasksManager.createEpic(titleAndDescription);

        System.out.print(Color.GREEN);
        System.out.println("Создан эпик с id = " + (epicTask.getId()));
        System.out.print(Color.RESET);

        // Создаем список названий и описаний подзадач
        String[] secondaryTitleAndDescriptions = {
                "Подзадача 1|Прочитать электронную почту|21.02.2023 12:24|15",
                "Подзадача 2|Прочитать мессенджеры|22.02.2023 12:39|15",
                "Подзадача 3|Прочитать соцсети|22.02.2023 12:59|15"};
        for (String titleDescription : secondaryTitleAndDescriptions) {
            // Создаем подзадачу
            Subtask subtask = fileBackedTasksManager.createSubtask(titleDescription, epicTask.getId());
            // Добавляем её к эпику
            //  epicTask = fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
            // fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
            if (subtask != null) {
                fileBackedTasksManager.addSubtaskToEpic(epicTask, subtask);
                System.out.print(Color.GREEN);
                System.out.println("Создана подзадача с id = " + subtask.getId());
                System.out.print(Color.RESET);
            } else {
                System.out.print(Color.RED);
                System.out.println("Подзадача не создана");
                System.out.print(Color.RESET);
            }
        }
        fileBackedTasksManager.setNeedWriteToFile(true);
        fileBackedTasksManager.addEpic(epicTask);
    }

    private static void getBrowsingHistory() {
        List<AbstractTask> historyFindTask = fileBackedTasksManager.getHistory();
        if (historyFindTask.isEmpty()) {
            System.out.print(Color.RED);
            System.out.println("История просмотров пуста");
            System.out.print(Color.RESET);

            return;
        }
        int count = 1;
        for (AbstractTask abstractTask : historyFindTask) {
            System.out.print(count + " ");
            System.out.println(abstractTask);
            count++;
            System.out.println("-------------------------------------------------------------------------------------");
        }
    }
}


