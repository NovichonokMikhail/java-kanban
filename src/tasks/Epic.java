package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        subtasks = new ArrayList<>();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatus();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void updateStatus() {
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