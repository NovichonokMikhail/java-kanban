package tasks;

import util.TaskStatus;
import util.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds;

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
        subtasksIds = new ArrayList<>();
    }

    public void setEndTime(LocalDateTime dateTime) {
        this.endTime = dateTime;
    }

    /**
     * Функция для удаления сабтаска из эпика
     * @param subtask сабтаск для удаления
     */
    public void removeSubtask(Subtask subtask) {
        subtasksIds.remove(subtask.getId());
    }

    /**
     * Функция возвращает список сабтасков эпика
     * @return {@code ArrayList<Subtask>} список сабтасков этого эпика
     */
    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    /**
     * Добвляет сабтаск в список сабтасков этого эпика
     * @param subtask сабтаск
     */
    public void addTask(Subtask subtask) {
        subtasksIds.add(subtask.getId());
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
                ", subtasksIds=" + subtasksIds +
                ", status=" + status +
                '}';
    }
}