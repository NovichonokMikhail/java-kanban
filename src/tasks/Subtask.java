package tasks;

import util.TaskStatus;
import util.TaskType;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Integer relatedEpicId;

    /**
     * Дефолтный конструктор без даты
     * @param name название
     * @param description описание
     * @param relatedEpicId эпик которму принадлежит этот сабтаск
     */
    public Subtask(String name, String description, Integer relatedEpicId) {
        this(name, description, TaskStatus.NEW, null, relatedEpicId, 0L, null);
    }

    /**
     * Дефолтный конструктор с датой начала задания
     * @param name название
     * @param description описание
     * @param relatedEpicId эпик которму принадлежит этот сабтаск
     * @param duration время выделенное на выполнение задачи в минутах
     * @param startTime дата и время начала задачи
     */
    public Subtask(String name, String description, Integer relatedEpicId, Long duration, LocalDateTime startTime) {
        this(name, description, TaskStatus.NEW, null, relatedEpicId, duration, startTime);
    }

    /**
     * Конструктор для выгрузки из файла
     * @param name название
     * @param description описание
     * @param status статус
     * @param id номер задачи
     * @param duration время выделенное на выполнение задачи в минутах
     * @param startTime дата и время начала задачи
     */
    public Subtask(String name,
                   String description,
                   TaskStatus status,
                   Integer id,
                   Integer relatedEpicId,
                   Long duration,
                   LocalDateTime startTime) {
        super(name, description, status, id, duration, startTime);
        this.relatedEpicId = relatedEpicId;
    }

    /**
     * Геттер для id эпика к которму относится задача
     * @return {@code Integer} id эпика
     */
    public Integer getRelatedEpicId() {
        return relatedEpicId;
    }

    /**
     * Возвращает тип задачи
     * @return {@code TaskType.SUBTASK}
     */
    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public void updateStatus(TaskStatus status) {
        super.updateStatus(status);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", relatedEpicId=" + relatedEpicId +
                ", status=" + status +
                '}';
    }
}