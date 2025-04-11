package tasks;

import util.TaskStatus;
import util.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected Integer id;
    protected TaskStatus status;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected Duration duration;

    public Task() {
        this("", "", TaskStatus.NEW, null, 0L, null);
    }

    /**
     * Дефолтный конструктор без даты и времени
     * @param name название
     * @param description описание
     */
    public Task(String name, String description) {
        this(name, description, TaskStatus.NEW, null, 0L, null);
    }

    /**
     * Дефолтный конструктор с указанием даты
     * @param name название
     * @param description описание
     * @param duration длительность в минутах
     * @param startTime начало задачи
     */
    public Task(String name, String description, Long duration, LocalDateTime startTime) {
        this(name, description, TaskStatus.NEW, null, duration, startTime);
    }

    /**
     * Конструктор для выгрузки из файла
     * @param name название
     * @param description описание
     * @param status статус
     * @param id номер задачи
     * @param duration - длительность в минутах
     * @param startTime - начало задачи
     */
    public Task(String name, String description, TaskStatus status, Integer id, Long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.duration = (duration != null) ? Duration.ofMinutes(duration) : Duration.ofMinutes(0L);
        this.startTime = startTime;
        if (startTime != null) endTime = startTime.plus(this.duration);
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

    public Duration getDuration() {
        return duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        if (startTime != null && duration != null) {
            endTime = startTime.plus(duration);
        } else {
            endTime = null;
        }
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
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