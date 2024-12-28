import tasks.Task;

import tasks.*;
import java.util.HashMap;

public class TaskManager {
    // хранение задач по типам
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    // переменная для хранения id
    private static int id;

    // конструктор с инициализацией всех переменных
    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        id = 0;
    }

    // public functions

    // функции для вывода всех задач по типам
    public void getAllTasks() {
        System.out.println("Printing tasks:");
        for (Integer i : tasks.keySet()) {
            System.out.println(tasks.get(i));
        }
        System.out.println();
    }

    public void getAllEpics() {
        System.out.println("Printing epics:");
        for (Integer i : epics.keySet()) {
            System.out.println(epics.get(i));
        }
        System.out.println();
    }

    public void getAllSubtasks() {
        System.out.println("Printing subtasks:");
        for (Integer i : subtasks.keySet()) {
            System.out.println(subtasks.get(i));
        }
        System.out.println();
    }


    // функции для очищения списка задач по типам
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }


    // получение задачи по id
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        System.out.println("Задачи с таким ID не существует в списке обычных задач");
        return null;
    }

    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        System.out.println("Задачи с таким ID не существует в списке эпиков");
        return null;
    }

    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        System.out.println("Задачи с таким ID не существует в списке подзадач");
        return null;
    }

    // удаление одной задачи по id
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            // удаление подзадачи из хэш-таблицы задач
            tasks.remove(id);
            System.out.println("Object have been deleted");
            return;
        }
        System.out.println("Id doesn't exist");
    }

    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            // удаление подзадач удаляемого эпика
            for (Subtask subtask : epics.get(id).getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
            // удаление эпика из общего хэш-таблицы эпиков
            epics.remove(id);
            System.out.println("Object have been deleted");
            return;
        }
        System.out.println("Id doesn't exist");
    }

    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            // удаление подзадачи из соответствующего эпика
            Subtask subtask = subtasks.get(id);
            Epic relatedEpic = subtask.getRelatedEpic();
            relatedEpic.removeSubtask(subtask);
            // удаление подзадачи из хэш-таблицы подзадач
            subtasks.remove(id);
            System.out.println("Object have been deleted");
            updateEpic(subtask.getRelatedEpic());
            return;
        }
        System.out.println("Id doesn't exist");
    }


    // обновление задач
    public void updateTask(Task task) {
        task.updateStatus();
    }

    public void updateSubtask(Subtask subtask) {
        subtask.updateStatus();
        updateEpic(subtask.getRelatedEpic());
    }

    public void updateEpic(Epic epic) {
        epic.updateStatus();
    }


    // создание новых задач
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    public void createSubtask(Subtask subtask) {
        Epic relatedEpic = subtask.getRelatedEpic();
        relatedEpic.addTask(subtask);
        subtasks.put(subtask.getId(), subtask);
    }

    // обновление и получение нового id
    public int generateId() {
        return id++;
    }

    public String getTaskType(int id) {
        if (tasks.containsKey(id)) {
            return "Task";
        } else if (epics.containsKey(id)) {
            return "Epic";
        } else if (subtasks.containsKey(id)) {
            return "Subtask";
        }
        return null;
    }

    // вывод меню
    public static void printMenu() {
        System.out.println("Меню:");
        System.out.println("1 - Вывести задачи");
        System.out.println("2 - Очистить задачи");
        System.out.println("3 - Получить задачу по id");
        System.out.println("4 - Создать задачу");
        System.out.println("5 - Обновить задачу");
        System.out.println("6 - Удалить задачу по id");
        System.out.println("7 - Завершить работу програмы");
    }
}
