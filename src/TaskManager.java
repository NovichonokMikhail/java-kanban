import tasks.Task;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    // хранение задач по типам
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    // переменная для хранения id
    private int id;

    // конструктор с инициализацией всех переменных
    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        id = 0;
    }

    // функции для вывода всех задач по типам
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Task task : tasks.values()) {
            tasksList.add(task);
        }
        return tasksList;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epicsList.add(epic);
        }
        return epicsList;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            subtaskList.add(subtask);
        }
        return subtaskList;
    }

    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        return epics.get(id).getSubtasks();
    }

    // функции для очищения списка задач по типам
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        // для каждой подзадачи
        for (Subtask subtask : subtasks.values()) {
            Epic relatedEpic = subtask.getRelatedEpic();
            // удаляем подзадачу из соответсвующего эпика
            relatedEpic.removeSubtask(subtask);
        }
        // очищаем список подзадач
        subtasks.clear();
    }


    // получение задачи по id
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        return null;
    }

    // удаление одной задачи по id
    public void deleteTaskById(int id) {
        // удаление подзадачи из хэш-таблицы задач
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        // удаление подзадач удаляемого эпика
        for (Subtask subtask : epics.get(id).getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
        // удаление эпика из общего хэш-таблицы эпиков
        epics.remove(id);
    }

    public void deleteSubtaskById(int id) {
        // удаление подзадачи из соответствующего эпика
        Subtask subtask = subtasks.get(id);
        Epic relatedEpic = subtask.getRelatedEpic();
        relatedEpic.removeSubtask(subtask);
        // удаление подзадачи из хэш-таблицы подзадач
        subtasks.remove(id);
        // обновление статуса эпика
        updateEpic(subtask.getRelatedEpic());
    }


    // обновление задач
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpic(subtask.getRelatedEpic());
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        epic.updateStatus();
    }


    // создание новых задач
    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        for (Subtask subtask : epic.getSubtasks()) {
            subtask.setId(generateId());
            subtasks.put(subtask.getId(), subtask);
        }
    }

    public void createSubtask(Subtask subtask) {
        Epic relatedEpic = subtask.getRelatedEpic();
        subtask.setId(generateId());
        relatedEpic.addTask(subtask);
        subtasks.put(subtask.getId(), subtask);
    }

    // обновление и получение нового id
    private int generateId() {
        return id++;
    }
}
