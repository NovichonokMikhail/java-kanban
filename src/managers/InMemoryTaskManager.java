package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

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
        tasksPriority = new TreeSet<>(Comparator.comparing(
                Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
        );
        id = 0;
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
    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        return epics.get(id).getSubtasks();
    }

    /**
     * Очищение всех задач
     */
    @Override
    public void deleteAllTasks() {
        tasks.keySet().stream().peek(id -> historyManager.remove(id));
        tasks.clear();
    }

    /**
     * Очищение всех эпиков и их подзадач
     */
    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        epics.keySet().stream().peek(id -> historyManager.remove(id));
        epics.clear();
    }

    /**
     * Очищение всех подзадач
     */
    @Override
    public void deleteAllSubtasks() {
        // для каждой подзадачи
        subtasks.values().stream()
                .peek(subtask -> {
                   subtask.getRelatedEpic().removeSubtask(subtask);
                   historyManager.remove(subtask.getId());
                });
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
        epics.get(id).getSubtasks().stream()
                .map(Subtask::getId)
                .forEach(sId -> {
                    subtasks.remove(sId);
                    historyManager.remove(sId);
                });
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
        final Epic relatedEpic = subtask.getRelatedEpic();
        relatedEpic.removeSubtask(subtask);
        // удаление подзадачи из хэш-таблицы подзадач
        subtasks.remove(id);
        historyManager.remove(id);
        // обновление статуса эпика
        updateEpic(relatedEpic);
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
        updateEpic(subtask.getRelatedEpic());
    }

    /**
     * Обновляет статус эпика
     * @param epic эпик статус которого надо обновить
     */
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        epic.updateStatus();
    }


    /**
     * Создает новую задачу
     * @param task объект новой задачи
     */
    @Override
    public void createTask(Task task) {
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
    public void createEpic(Epic epic) {
        if (epic.getId() == null || epics.containsKey(epic.getId())) { // проверка конфликта
            while (epics.containsKey(id)) {
                generateId();
            }
            epic.setId(generateId());
        }

        epics.put(epic.getId(), epic);
        for (Subtask subtask : epic.getSubtasks()) {
            createSubtask(subtask);
            tasksPriority.add(subtask);
        }
        tasksPriority.add(epic);
    }

    /**
     * Создает новый сабтаск
     * @param subtask объект нового сабтаска
     */
    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask.getId() == null || subtasks.containsKey(subtask.getId())) {
            while (subtasks.containsKey(id)) {
                generateId();
            }
            subtask.setId(generateId());
        }
        subtasks.put(subtask.getId(), subtask);
        tasksPriority.add(subtask);
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
}
