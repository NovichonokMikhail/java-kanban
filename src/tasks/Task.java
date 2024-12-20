package tasks;

import java.util.Objects;
import java.util.Scanner;

public class Task {
    final String name;
    final String description;
    final int id;
    TaskStatus status;
    final static Scanner scanner = new Scanner(System.in);

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = Objects.hash(name, description);
        status = TaskStatus.NEW;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = Objects.hash(name, description);
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public static Task getTask() {
        System.out.print("Enter Task name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Task description: ");
        String description = scanner.nextLine();
        return new Task(name, description);
    }

    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return Objects.equals(id, otherTask.getId());
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}