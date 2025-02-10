package tasks;

import util.TaskStatus;
import util.TaskType;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

    /**
     * Дефолтный конструктор
     * @param name название задачи
     * @param description описание
     */
    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    /**
     * Конструктор с указанием id для упрощения возсоздание эпика из файла
     * @param name название задачи
     * @param description описание
     * @param id id задачи
     */
    public Epic(String name, String description, int id) {
        super(name, description, TaskStatus.NEW, id);
        subtasks = new ArrayList<>();
    }

    /**
     * Функция для удаления сабтаска из эпика
     * @param subtask сабтаск для удаления
     */
    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatus();
    }

    /**
     * Функция возвращает список сабтасков эпика
     * @return {@code ArrayList<Subtask>} список сабтасков этого эпика
     */
    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    /**
     * Функция для обновления статуса эпика
     */
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

    /**
     * Добвляет сабтаск в список сабтасков этого эпика
     * @param subtask сабтаск
     */
    public void addTask(Subtask subtask) {
        subtasks.add(subtask);
        updateStatus();
    }

    /**
     * Возвращает тип задачи
     * @return {@code TaskType.EPIC}
     */
    @Override
    public TaskType getType() {
        return TaskType.EPIC;
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