package managers;

import exceptions.TimeIntervalOccupiedException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    // хранение задач по типам
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, Subtask> subtasks;
    protected TreeSet<Task> tasksPriority;

    // менеджер для хранения истории
    protected HistoryManager historyManager;

    // переменная для хранения id
    protected int id;

    /**
     * Дефолтный конструктор
     */
    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        tasksPriority = new TreeSet<>(
                Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
        id = 0;
        setHistoryManager(Managers.getDefaultHistory());
    }

    /**
     * Сеттер для менеджера истории
     * @param historyManager менеджер истории который реализует интерфейс {@code HistoryManager}
     */
    @Override
    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    /**
     * Функция для получения всех задач типа {@code Task}
     * @return {@code ArrayList<Task>}
     */
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Функция для получения всех задач типа {@code Epic}
     * @return {@code ArrayList<Epic>}
     */
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Функция для получения всех задач типа {@code Subtask}
     * @return {@code ArrayList<Subtask>}
     */
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    /**
     * Возвращает сабтаски эпика по его id
     * @param id номер эпика
     * @return {@code ArrayList<Subtask>} список сабтасков
     */
    @Override
    public List<Subtask> getSubtasksByEpicId(int id) {
        return epics.get(id)
                .getSubtasksIds()
                .stream()
                .map(subtasks::get)
                .toList();
    }

    /**
     * Очищение всех задач
     */
    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    /**
     * Очищение всех эпиков и их подзадач
     */
    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        epics.keySet().forEach(historyManager::remove);
        epics.clear();
    }

    /**
     * Очищение всех подзадач
     */
    @Override
    public void deleteAllSubtasks() {
        // для каждой подзадачи
        Collection<Subtask> subtaskList = subtasks.values();
        for (Subtask s : subtaskList) {
            historyManager.remove(s.getId());
            epics.get(s.getRelatedEpicId()).removeSubtask(s);
        }
        // очищаем список подзадач
        subtasks.clear();
    }


    /**
     * Получение задачи по ее номеру
     * @param id номер задачи
     * @return {@code Task} объект задачи с id если такая есть
     */
    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        }
        return null;
    }

    /**
     * Получение эпика по его номеру
     * @param id номер задачи
     * @return {@code Epic} объект эпика с id если такой есть
     */
    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epic;
        }
        return null;
    }


    /**
     * Получение подзадачи по ее номеру
     * @param id номер задачи
     * @return {@code Subtask} объект подзадачи с id если такая есть
     */
    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            historyManager.add(subtask);
            return subtask;
        }
        return null;
    }

    /**
     * Удаление задачи по ее номеру
     * @param id номер задачи
     */
    @Override
    public void deleteTaskById(int id) {
        // удаление подзадачи из хэш-таблицы задач
        tasks.remove(id);
        historyManager.remove(id);
    }

    /**
     * Удаление эпика по его номеру
     * @param id номер эпика
     */
    @Override
    public void deleteEpicById(int id) {
        // удаление подзадач удаляемого эпика
        Epic e = epics.get(id);
        for (Subtask subtask : getSubtasksByEpicId(id)) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
            e.removeSubtask(subtask);
        }
        // удаление эпика из общего хэш-таблицы эпиков
        epics.remove(id);
        historyManager.remove(id);
    }

    /**
     * Удаление побзадачи по ее номеру
     * @param id номер побзадачи
     */
    @Override
    public void deleteSubtaskById(int id) {
        // удаление подзадачи из соответствующего эпика
        final Subtask subtask = subtasks.get(id);
        final Epic relatedEpic = epics.get(subtask.getRelatedEpicId());
        if (relatedEpic != null) {
            relatedEpic.removeSubtask(subtask);
            // удаление подзадачи из хэш-таблицы подзадач
            subtasks.remove(id);
            historyManager.remove(id);
            // обновление статуса эпика
            updateEpic(relatedEpic);
        }
    }


    /**
     * Обновляет текущий таск
     * @param task таск который надо обновить
     */
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    /**
     * Обновляет текущий сабтаск
     * @param subtask таск который надо обновить
     */
    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpic(epics.get(subtask.getRelatedEpicId()));
    }

    /**
     * Обновляет статус эпика
     * @param epic эпик статус которого надо обновить
     */
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        updateStartAndEndTimes(epic);
    }

    /**
     * Создает новую задачу
     * @param task объект новой задачи
     */
    @Override
    public void createTask(Task task) throws TimeIntervalOccupiedException {
        if (intersectsAnyTask(task))
            throw new TimeIntervalOccupiedException("Этот интервал уже занят");
        if (task.getId() == null || tasks.containsKey(task.getId())) {
            while (tasks.containsKey(id)) {
                generateId();
            }
            task.setId(generateId());
        }
        tasksPriority.add(task);
        tasks.put(task.getId(), task);
    }

    /**
     * Создает новый эпик
     * @param epic объект нового эпика
     */
    @Override
    public void createEpic(Epic epic) throws TimeIntervalOccupiedException {
        if (intersectsAnyTask(epic))
            throw new TimeIntervalOccupiedException("Этот интервал уже занят");
        if (epic.getId() == null || epics.containsKey(epic.getId())) { // проверка конфликта
            while (epics.containsKey(id)) {
                generateId();
            }
            epic.setId(generateId());
        }

        epics.put(epic.getId(), epic);
        for (Subtask subtask : getSubtasksByEpicId(epic.getId())) {
            createSubtask(subtask);
            tasksPriority.add(subtask);
        }
        tasksPriority.add(epic);
        updateEpicStatus(epic);
        updateEpicStatus(epic);
    }

    /**
     * Создает новый сабтаск
     * @param subtask объект нового сабтаска
     */
    @Override
    public void createSubtask(Subtask subtask) throws TimeIntervalOccupiedException {
        if (intersectsAnyTask(subtask))
            throw new TimeIntervalOccupiedException("Этот интервал уже занят");
        if (subtask.getId() == null || subtasks.containsKey(subtask.getId())) {
            while (subtasks.containsKey(id)) {
                generateId();
            }
            subtask.setId(generateId());
        }
        Epic e = epics.get(subtask.getRelatedEpicId());
        if (e != null) {
            e.addTask(subtask);
            subtasks.put(subtask.getId(), subtask);
            tasksPriority.add(subtask);
            updateEpicStatus(e);
            updateStartAndEndTimes(e);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return tasksPriority.stream().filter(t -> t.getStartTime() != null).toList();
    }

    /**
     * Внутренняя функция для генерации id задач
     * @return {@code int} номер задачи
     */
    protected int generateId() {
        return id++;
    }

    protected void updateEpicStatus(final Epic e) {
        if (e != null) {
            final List<Subtask> subtasks = getSubtasksByEpicId(e.getId());
            if (subtasks.isEmpty()) {
                e.updateStatus(TaskStatus.NEW);
                return;
            }
            int tasksDone = subtasks.stream()
                    .filter(s -> s.getStatus() == TaskStatus.DONE)
                    .toList().size();
            int tasksInProgress = subtasks.stream()
                    .filter(s -> s.getStatus() == TaskStatus.IN_PROGRESS)
                    .toList().size();
            if (tasksDone == subtasks.size()) e.updateStatus(TaskStatus.DONE);
            else if (tasksDone > 0 || tasksInProgress > 0) e.updateStatus(TaskStatus.IN_PROGRESS);
            else e.updateStatus(TaskStatus.NEW);
        }
    }

    protected boolean intersectsAnyTask(Task task) {
        return Stream.of(tasks, epics, subtasks)
                .flatMap(map -> map.values().stream())
                .anyMatch(t -> TaskManager.intersectsTask(t, task));
    }

    protected void updateStartAndEndTimes(Epic e) {
        List<Subtask> nonEmptyTimes = e.getSubtasksIds().stream()
                .map(this::getSubtask)
                .filter(s -> s.getStartTime() != null).toList();
        if (nonEmptyTimes.isEmpty()) {
            e.setStartTime(null);
            e.setEndTime(null);
        } else if (nonEmptyTimes.size() == 1) {
            Subtask s = this.getSubtask(e.getSubtasksIds().getFirst());
            e.setStartTime(s.getStartTime());
            e.setEndTime(s.getEndTime());
        } else {
            LocalDateTime startTime = nonEmptyTimes.stream()
                    .map(Subtask::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .get();
            LocalDateTime endTime = nonEmptyTimes.stream()
                    .map(Subtask::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .get();
            e.setStartTime(startTime);
            e.setEndTime(endTime);
        }
    }
}
