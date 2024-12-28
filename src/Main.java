import tasks.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // часто используемые выводы
        final String UNKNOWN_TYPE_ERROR = "Такого типа не существует.";
        final String UNKNOWN_ID_ERROR = "Задачи с таким id не существует";
        final String TYPES_OPTIONS = "1 - Задачи, 2 - Эпики, 3 - Подзадачи";

        // инициализация нужных классов
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();


        // основной цикл
        while (true) {
            // переменные для хранения id и тип задачи которые введет пользоватьель
            int command; // команда
            int id; // введенное id пользователем
            int taskType; // введенный тип задачи от пользователя

            // вывод меню
            TaskManager.printMenu();
            command = scanner.nextInt();
            switch (command) {
                case 1:
                    // вывод всех задач в зависимости от выбранного типа
                    System.out.println("Вывести все: " + TYPES_OPTIONS);
                    taskType = scanner.nextInt();
                    switch (taskType) {
                        case 1:
                            taskManager.getAllTasks();
                            break;
                        case 2:
                            taskManager.getAllEpics();
                            break;
                        case 3:
                            taskManager.getAllSubtasks();
                            break;
                        default:
                            System.out.println(UNKNOWN_TYPE_ERROR);
                    }
                    break;
                case 2:
                    // удаление всех задач в зависимости от типа
                    System.out.println("Удалить все: " + TYPES_OPTIONS);
                    taskType = scanner.nextInt();
                    switch (taskType) {
                        case 1:
                            taskManager.deleteAllTasks();
                            break;
                        case 2:
                            taskManager.deleteAllEpics();
                            break;
                        case 3:
                            taskManager.deleteAllSubtasks();
                            break;
                        default:
                            System.out.println(UNKNOWN_TYPE_ERROR);
                    }
                    break;
                case 3:
                    // получение задачи по id
                    System.out.print("Введите id: ");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    switch (taskManager.getTaskType(id)) {
                        case "Task":
                            taskManager.getTask(id);
                            break;
                        case "Epic":
                            taskManager.getEpic(id);
                            break;
                        case "Subtask":
                            taskManager.getSubtask(id);
                            break;
                        default:
                            System.out.println(UNKNOWN_ID_ERROR);
                    }
                    break;
                case 4:
                    System.out.println("Какой тип задачи вы хотите создать?");
                    System.out.println(TYPES_OPTIONS);
                    taskType = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("Введите название задачи.");
                    String name = scanner.nextLine();
                    System.out.println("Введите описание.");
                    String description = scanner.nextLine();
                    switch (taskType) {
                        case 1:
                            taskManager.createTask(
                                    new Task(name, description, taskManager.generateId()
                                    ));
                            break;
                        case 2:
                            Epic epic = new Epic(name, description, taskManager.generateId());
                            System.out.println("Из скольки подзадач состоит эпик?");
                            int subtaskCount = scanner.nextInt();
                            scanner.nextLine();
                            for (int i = 0; i < subtaskCount; i++) {
                                System.out.println("Введите название подзадачи:");
                                name = scanner.nextLine();
                                System.out.println("Введите описание подзадачи:");
                                description = scanner.nextLine();
                                epic.addTask(new Subtask(
                                        name
                                        , description
                                        , epic
                                        , taskManager.generateId()
                                ));
                            }
                            taskManager.createEpic(epic);
                            break;
                        case 3:
                            System.out.println("Введите id эпика которму будет принадлежать подзадача:");
                            id = scanner.nextInt();
                            scanner.nextLine();
                            if (taskManager.getEpic(id) != null) {
                                taskManager.createSubtask(
                                        new Subtask(name
                                                , description
                                                , taskManager.getEpic(id)
                                                , taskManager.generateId()
                                        ));
                            }
                            break;
                        default:
                            System.out.println(UNKNOWN_TYPE_ERROR);
                    }
                    break;
                case 5:
                    System.out.print("Введите id задачи для обновления: ");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    switch (taskManager.getTaskType(id)) {
                        case "Task":
                            taskManager.updateTask(
                                    taskManager.getTask(id)
                            );
                            break;
                        case "Epic":
                            System.out.println("Задачи типа эпик обновляются автоматически " +
                                    "вместе с выполнением их подзадач " +
                                    "и не могут быть обновлены пользователем");
                            break;
                        case "Subtask":
                            taskManager.updateSubtask(
                                    taskManager.getSubtask(id)
                            );
                            break;
                        default:
                            System.out.println(UNKNOWN_ID_ERROR);
                    }
                    break;
                case 6:
                    System.out.print("Введите id: ");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    switch (taskManager.getTaskType(id)) {
                        case "Task":
                            taskManager.deleteTaskById(id);
                            break;
                        case "Epic":
                            taskManager.deleteEpicById(id);
                            break;
                        case "Subtask":
                            taskManager.deleteSubtaskById(id);
                            break;
                        default:
                            System.out.println(UNKNOWN_ID_ERROR);
                    }
                    break;
                case 7:
                    System.out.println("Работа программы завершена.");
                    System.out.println("Спасибо за использование!");
                    return;
                default:
                    System.out.println("Такой команды не существует");
            }
        }
    }
}