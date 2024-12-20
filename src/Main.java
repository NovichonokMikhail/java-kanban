import tasks.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();
        int id;
        while (true) {
            TaskManager.printMenu();
            int command = scanner.nextInt();
            switch (command) {
                case 1:
                    taskManager.printTasks();
                    break;
                case 2:
                    taskManager.clearTasks();
                    break;
                case 3:
                    System.out.println("Enter Task id: ");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.getTask(id);
                    break;
                case 4:
                    Task task = taskManager.createTask();
                    if (task != null) {
                        taskManager.addTask(task);
                    } else {
                        System.out.println("Try again.");
                    }
                    break;
                case 5:
                    System.out.print("Enter id of Task to replace: ");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.update(id);
                    break;
                case 6:
                    System.out.println("Enter Task id: ");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.remove(id);
                    break;
                case 7:
                    return;
                default:
                    System.out.println("This command doesn't exist");
            }
        }
    }
}
