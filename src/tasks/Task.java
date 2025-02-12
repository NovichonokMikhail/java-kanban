package tasks;

import util.TaskStatus;
import util.TaskType;

import java.util.Objects;

public class Task {
    protected final String name;
    protected final String description;
    protected Integer id;
    protected TaskStatus status;

    /**
     * Дефолтный конструктор
     * @param name название
     * @param description описание
     */
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = TaskStatus.NEW;
    }

    /**
     * Конструктор для выгрузки из файла
     * @param name название
     * @param description описание
     * @param status статус
     * @param id номер задачи
     */
    public Task(String name, String description, TaskStatus status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    /**
     * Возвращает тип задачи
     * @return {@code TaskType.TASK}
     */
    public TaskType getType() {
        return TaskType.TASK;
    }

    /**
     * Геттер для названия задачи
     * @return {@code String} название
     */
    public String getName() {
        return name;
    }

    /**
     * Геттер для описания задачи
     * @return {@code String} описание
     */
    public String getDescription() {
        return description;
    }

    /**
     * Геттер для id задачи
     * @return {@code int} номер задачи
     */
    public Integer getId() {
        return id;
    }

    /**
     * Сеттер id задачи
     * @param id новый id для задачи
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Геттер для статуса задачи
     * @return {@code TaskStatus} текущийСтатус
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Сеттер статуса задачи
     * @param status новый статус
     */
    public void updateStatus(TaskStatus status) {
        this.status = status;
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
        return id;
    }
}