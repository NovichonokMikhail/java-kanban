package managers;

import tasks.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    void setHistoryManager(HistoryManager manager);

    List<Task> getHistory();

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Subtask> getSubtasksByEpicId(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);
}
