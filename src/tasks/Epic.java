package tasks;

import java.util.ArrayList;
import java.util.Scanner;

public class Epic extends Task {

    private final static Scanner scanner = new Scanner(System.in);
    private final ArrayList<Subtask> subtasks;

    Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void printSubtasks() {
        System.out.println(subtasks);
    }

    void updateStatus() {
        int tasksDone = 0;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == TaskStatus.DONE) {
                tasksDone++;
            } else if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                status = TaskStatus.IN_PROGRESS;
            }
        }

        if (tasksDone == 0) {
            status = TaskStatus.NEW;
        } else if (tasksDone == subtasks.size()) {
            status = TaskStatus.DONE;
        }
    }

    public void addTask(Subtask subtask) {
        subtasks.add(subtask);
        updateStatus();
    }

    public static Epic getEpic() {
        System.out.println("How many subtasks does your task have?");
        int subtasksAmount = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter name for your epic:");
        String epicName = scanner.nextLine();
        System.out.println("Enter description for your epic");
        String epicDescription = scanner.nextLine();
        Epic epic = new Epic(epicName, epicDescription);
        System.out.println("Creating subtasks");
        for (int i = 0; i < subtasksAmount; i++) {
            System.out.print("Enter name: ");
            String subtaskName = scanner.nextLine();
            System.out.println("Enter description");
            String subtaskDescription = scanner.nextLine();
            epic.addTask(new Subtask(subtaskName, subtaskDescription, epic));
        }
        return epic;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", subtasks=" + subtasks.size() +
                ", status=" + status +
                '}';
    }
}