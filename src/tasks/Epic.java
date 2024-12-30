package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatus();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void updateStatus() {
        boolean allTasksDone = true;
        boolean allTasksNotDone = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                allTasksDone = false;
            } else if (subtask.getStatus() != TaskStatus.NEW) {
                allTasksNotDone = false;
            }
        }

        if (allTasksDone && !allTasksNotDone) { // !allTasksNotDone на случай если эпик будет пустым
            status = TaskStatus.DONE;
        } else if (allTasksNotDone) {
            status = TaskStatus.NEW;
        } else {
            status = TaskStatus.IN_PROGRESS;
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