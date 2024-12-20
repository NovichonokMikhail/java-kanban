import tasks.Task;

import tasks.*;
import java.util.HashMap;
import java.util.Scanner;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final Scanner scanner;

    TaskManager() {
        tasks = new HashMap<>();
        scanner = new Scanner(System.in);
    }

    public void printTasks() {
        System.out.println("Printing tasks:");
        for (Integer i : tasks.keySet()) {
            System.out.println(tasks.get(i));
        }
        System.out.println();
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void getTask(int id) {
        System.out.println(tasks.get(id));
    }

    public void remove(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            System.out.println("Object have been deleted");
            return;
        }
        System.out.println("Id doesn't exist");
    }

    public void update(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("There's no task with this id");
            return;
        }

        System.out.println("Enter new status: IN_PROGRESS or DONE");
        String strStatus = scanner.nextLine().toUpperCase();
        TaskStatus newStatus = TaskStatus.valueOf(strStatus);

        Task oldTask = tasks.get(id);
        Task updatedTask;
        if (oldTask.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) oldTask;
            Epic relatedEpic = subtask.getRelatedEpic();
            relatedEpic.removeSubtask(subtask);
            tasks.remove(id);

            updatedTask = new Subtask(subtask.getName()
                    , subtask.getDescription()
                    , subtask.getRelatedEpic(), newStatus);
            relatedEpic.addTask((Subtask) updatedTask);
        } else if (oldTask.getClass() == Epic.class) {
            System.out.println("Status of Epic Task cannot be updated manually");
            updatedTask = oldTask;
        } else {
            tasks.remove(id);
            updatedTask = new Task(oldTask.getName(), oldTask.getDescription(), newStatus);
        }
        tasks.put(id, updatedTask);
    }

    public Task createTask() {
        System.out.println("Choose type of the task: 1 - Regular, 2 - Epic (consists of subtasks)");
        int type = scanner.nextInt();
        scanner.nextLine();
        return switch (type) {
            case 1 -> Task.getTask();
            case 2 -> Epic.getEpic();
            default -> null;
        };
    }

    public void addTask(Task task) {
        if (task.getClass() == Epic.class) {
            Epic epic = (Epic) task;
            for (Subtask subtask : epic.getSubtasks()) {
                tasks.put(subtask.getId(), subtask);
            }
        }
        tasks.put(task.getId(), task);
    }

    public static void printMenu() {
        System.out.println("Menu:");
        System.out.println("1 - print all tasks");
        System.out.println("2 - clear tasks");
        System.out.println("3 - get task by id");
        System.out.println("4 - create new task");
        System.out.println("5 - update task");
        System.out.println("6 - remove task by id");
        System.out.println("7 - exit program");
    }
}
