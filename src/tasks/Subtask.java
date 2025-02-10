package tasks;

import util.TaskStatus;
import util.TaskType;

public class Subtask extends Task {
    private final Epic relatedEpic;

    /**
     * Дефолтный конструктор
     * @param name название
     * @param description описание
     * @param relatedEpic эпик которму принадлежит этот сабтаск
     */
    public Subtask(String name, String description, Epic relatedEpic) {
        super(name, description);
        this.relatedEpic = relatedEpic;
        relatedEpic.addTask(this);
    }

    /**
     * Конструктор для выгрузки из файла
     * @param name название
     * @param description описание
     * @param status статус
     * @param id номер задачи
     * @param relatedEpicId id эпика к которому принадлежит задача
     */
    public Subtask(String name, String description, TaskStatus status, int id, Epic relatedEpic) {
        super(name, description, status, id);
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
}