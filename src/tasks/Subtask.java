package tasks;

import util.TaskStatus;
import util.TaskType;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Epic relatedEpic;

    /**
     * Дефолтный конструктор без даты
     * @param name название
     * @param description описание
     * @param relatedEpic эпик которму принадлежит этот сабтаск
     */
    public Subtask(String name, String description, Epic relatedEpic) {
        this(name, description, TaskStatus.NEW, null, relatedEpic, 0L, null);
    }

    /**
     * Дефолтный конструктор с датой начала задания
     * @param name название
     * @param description описание
     * @param relatedEpic эпик которму принадлежит этот сабтаск
     * @param duration время выделенное на выполнение задачи в минутах
     * @param startTime дата и время начала задачи
     */
    public Subtask(String name, String description, Epic relatedEpic, Long duration, LocalDateTime startTime) {
        this(name, description, TaskStatus.NEW, null, relatedEpic, duration, startTime);
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
                   Epic relatedEpic,
                   Long duration,
                   LocalDateTime startTime) {
        super(name, description, status, id, duration, startTime);
        this.relatedEpic = relatedEpic;
        relatedEpic.addTask(this);
    }

    /**
     * Геттер для эпика к которму относится задача
     * @return {@code Epic} id эпика
     */
    public Epic getRelatedEpic() {
        return relatedEpic;
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
        relatedEpic.updateStatus();
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", relatedEpic=" + relatedEpic +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean intersectsTask(Task task) {
        if (task instanceof Epic e) {
            if (e.equals(relatedEpic)) return false;
        }
        return super.intersectsTask(task);
    }
}