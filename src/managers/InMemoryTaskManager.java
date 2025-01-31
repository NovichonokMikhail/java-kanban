package managers;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    // хранение задач по типам
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    // менеджер для хранения истории
    private HistoryManager historyManager;

    // переменная для хранения id
    private int id;

    // конструктор с инициализацией всех переменных
    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        id = 0;
    }

    @Override
    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // функции для вывода всех задач по типам
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        return epics.get(id).getSubtasks();
    }

    // функции для очищения списка задач по типам
    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        // для каждой подзадачи
        for (Subtask subtask : subtasks.values()) {
            Epic relatedEpic = subtask.getRelatedEpic();
            // удаляем подзадачу из соответсвующего эпика
            relatedEpic.removeSubtask(subtask);
            historyManager.remove(subtask.getId());
        }
        // очищаем список подзадач
        subtasks.clear();
    }


    // получение задачи по id
    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epic;
        }
        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            historyManager.add(subtask);
            return subtask;
        }
        return null;
    }

    // удаление одной задачи по id
    @Override
    public void deleteTaskById(int id) {
        // удаление подзадачи из хэш-таблицы задач
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        // удаление подзадач удаляемого эпика
        for (Subtask subtask : epics.get(id).getSubtasks()) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        // удаление эпика из общего хэш-таблицы эпиков
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        // удаление подзадачи из соответствующего эпика
        Subtask subtask = subtasks.get(id);
        Epic relatedEpic = subtask.getRelatedEpic();
        relatedEpic.removeSubtask(subtask);
        // удаление подзадачи из хэш-таблицы подзадач
        subtasks.remove(id);
        historyManager.remove(id);
        // обновление статуса эпика
        updateEpic(subtask.getRelatedEpic());
    }


    // обновление задач
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpic(subtask.getRelatedEpic());
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        epic.updateStatus();
    }


    // создание новых задач
    @Override
    public void createTask(Task task) {
        if (task.getId() == null || tasks.containsKey(task.getId())) {
            while (tasks.containsKey(id)) {
                generateId();
            }
            task.setId(generateId());
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic.getId() == null || epics.containsKey(epic.getId())) {
            while (epics.containsKey(id)) {
                generateId();
            }
            epic.setId(generateId());
        }

        epics.put(epic.getId(), epic);
        for (Subtask subtask : epic.getSubtasks()) {
            createSubtask(subtask);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask.getId() == null || subtasks.containsKey(subtask.getId())) {
            while (subtasks.containsKey(id)) {
                generateId();
            }
            subtask.setId(generateId());
        }
        subtasks.put(subtask.getId(), subtask);
    }

    // обновление и получение нового id
    private int generateId() {
        return id++;
    }
}
