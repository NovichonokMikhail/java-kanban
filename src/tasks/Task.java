package tasks;

import java.util.Objects;

public class Task {
    protected final String name;
    protected final String description;
    protected final int id;
    protected TaskStatus status;

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        status = TaskStatus.NEW;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void updateStatus() {
        status = TaskStatus.DONE;
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

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}