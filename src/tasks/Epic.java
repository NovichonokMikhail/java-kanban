package tasks;

import util.TaskStatus;
import util.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

    /**
     * Дефолтный конструктор без даты начала
     * @param name название задачи
     * @param description описание
     */
    public Epic(String name, String description) {
        this(name, description, null, 0L, null);
    }

    /**
     * Конструктор для выгрузки из файла
     * @param name название задачи
     * @param description описание
     * @param id id задачи
     */
    public Epic(String name, String description, Integer id, Long duration, LocalDateTime startTime) {
        super(name, description, TaskStatus.NEW, id, duration, startTime);
        subtasks = new ArrayList<>();
    }

    /**
     * Функция для удаления сабтаска из эпика
     * @param subtask сабтаск для удаления
     */
    public void removeSubtask(Subtask subtask) {
        if (subtasks.contains(subtask)) {
            subtasks.remove(subtask);
            updateStatus();
            updateStartAndEndTimes();
            duration.minus(subtask.getDuration());
        }
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
        if (subtasks.isEmpty()) {
            status = TaskStatus.NEW;
            return;
        }

        int tasksDone = subtasks.stream()
                .filter(s -> s.status == TaskStatus.DONE)
                .toList().size();

        int tasksInProgress = subtasks.stream()
                .filter(s -> s.status == TaskStatus.IN_PROGRESS)
                .toList().size();

        if (tasksDone == subtasks.size()) status = TaskStatus.DONE;
        else if (tasksDone > 0 || tasksInProgress > 0) status = TaskStatus.IN_PROGRESS;
        else status = TaskStatus.NEW;
    }

    /**
     * Добвляет сабтаск в список сабтасков этого эпика
     * @param subtask сабтаск
     */
    public void addTask(Subtask subtask) {
        subtasks.add(subtask);
        updateStatus();
        duration.plus(subtask.getDuration());
        updateStartAndEndTimes();
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

    @Override
    public boolean intersectsTask(Task task) {
        if (task instanceof Subtask) {
            if (subtasks.contains((Subtask) task)) return false;
        }
        return super.intersectsTask(task);
    }

    private void updateStartAndEndTimes() {
        List<Subtask> nonEmptyTimes = subtasks.stream().filter(s -> s.startTime != null).toList();
        if (nonEmptyTimes.isEmpty()) {
            startTime = null;
            endTime = null;
        } else if (nonEmptyTimes.size() == 1) {
            Subtask s = subtasks.getFirst();
            startTime = s.startTime;
            endTime = s.endTime;
        } else {
            startTime = nonEmptyTimes.stream().map(s -> s.startTime).min(LocalDateTime::compareTo).get();
            System.out.println(startTime);
            endTime = nonEmptyTimes.stream().map(s -> s.endTime).max(LocalDateTime::compareTo).get();
            System.out.println(endTime);
        }
    }
}