import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static Scanner scanner;
    static TaskManager taskManager = TaskManager.getInstance();

    public static void main(String[] args) {
        //Scanner scanner = new Scanner(System.in);
        scanner = new Scanner(System.in);
        // TaskManager taskManager = TaskManager.getInstance();
        String userInput;

        do {
            printMenu();
            userInput = scanner.nextLine();
            switch (userInput) {
                case "1" -> {
                    boolean tasksAreExisting = taskManager.getListOfAllTasks();
                    if (!tasksAreExisting) {
                        System.out.print(Color.RED);
                        System.out.println("У Вас нет задач");
                        System.out.print(Color.RESET);
                    }
                }
                case "2" -> createTask();
                case "3" -> {
                    System.out.println("Ведите id задачи");

                    String stringId = scanner.nextLine();
                    int id = stringToInt(stringId);
                    boolean taskIsFound = taskManager.findTaskById(id);

                    if (!taskIsFound) {
                        System.out.print(Color.RED);
                        System.out.println("У Вас нет задач с таким id");
                        System.out.print(Color.RESET);
                    }
                }
                case "4" -> {
                    System.out.println("Ведите id задачи, которую хотите обновить");
                    System.out.println("Если это подзадача, то введите номер родительского эпика");

                    String stringId = scanner.nextLine();
                    int id = stringToInt(stringId);

                    //taskManager.updateTaskById(id);
                }
                case "5" -> {
                    System.out.println("Ведите id задачи");

                    String stringId = scanner.nextLine();
                    int id = stringToInt(stringId);
                    boolean taskIsFound = taskManager.deleteTaskById(id);

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
                case "6" -> {
                    boolean taskExists = taskManager.deleteAllTasks();

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
                case "0" -> System.out.println("Выход");
                default -> System.out.println("Извините, такой команды пока нет. Введите число от 0 до 6");
            }
        } while (!userInput.equals("0"));
    }

    static void printMenu() {
        System.out.println();
        System.out.println("1 - Получить список всех задач");
        System.out.println("2 - Создать задачу");
        System.out.println("3 - Найти задачу по идентификатору");
        System.out.println("4 - Обновить задачу");
        System.out.println("5 - Удалить задачу по идентификатору");
        System.out.println("6 - Удалить все задачи");
        System.out.println("0 - Выход из программы");
    }

    static int stringToInt(String userInput) {
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
        int taskType = stringToInt(userInput);

        for (int value : values) {
            if (taskType == value) return taskType;
        }
        System.out.println("Такого варианта пока нет");
        return -1;
    }

    /*private static String[] titleAndDescription() {
        String[] result = new String[2];
        System.out.println("Введите название задачи");
        result[0] = scanner.nextLine();
        System.out.println("Введите описание задачи");
        result[1] = scanner.nextLine();
        return result;
    }*/

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
            Task task = taskManager.createStandardTask(titleAndDescription());
            System.out.print(Color.GREEN);
            System.out.println("Создана обычная задача с id = " + task.getId());
            System.out.print(Color.RESET);
        } else if (typeTask == 2) {
            EpicTask epicTask = taskManager.createEpic(titleAndDescription());
            System.out.print(Color.GREEN);
            System.out.println("Создан эпик с id = " + (epicTask.getId()));
            System.out.print(Color.RESET);
            // Получаем список названий и описаний подзадач
            List<String> titleAndDescriptions = createSubtaskItemInfo();
            for (String titleAndDescription : titleAndDescriptions) {
                // Создаем подзадачу
                epicTask = taskManager.addSubtaskEpic(epicTask, titleAndDescription);
            }
            // Кладем эпик в мапу
            taskManager.addEpic(epicTask);
        }
    }
}
