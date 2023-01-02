import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = TaskManager.getInstance();
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
                case "2" -> taskManager.createTask();
                case "3" -> {
                    System.out.println("Ведите id задачи");
                    String stringId = scanner.nextLine();
                    int id = Integer.parseInt(stringId);
                    boolean taskIsFound = taskManager.findTaskById(id);
                    if (!taskIsFound) {
                        System.out.print(Color.RED);
                        System.out.println("У Вас нет задач с таким id");
                        System.out.print(Color.RESET);
                    }
                }
                case "4" -> {
                    System.out.println("Ведите id задачи, которую хотите обновить");
                    String stringId = scanner.nextLine();
                    int id = Integer.parseInt(stringId);
                    taskManager.updateTaskById(id);
                }
                case "5" -> taskManager.deleteTaskById(1);
                case "6" -> taskManager.deleteAllTasks();
                case "0" -> System.out.println("Выход");
                default ->  System.out.println("Извините, такой команды пока нет. Введите число от 0 до 6");
            };
            
        } while (!userInput.equals("0"));
    }

    static void printMenu() {
        System.out.println("1 - Получить список всех задач");
        System.out.println("2 - Создать задачу");
        System.out.println("3 - Найти задачу по идентификатору");
        System.out.println("4 - Обновить задачу");
        System.out.println("5 - Удалить задачу по идентификатору");
        System.out.println("6 - Удалить все задачи");
        System.out.println("0 - Выход из программы");
    }

}
