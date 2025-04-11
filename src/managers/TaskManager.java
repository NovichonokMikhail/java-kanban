package managers;

import exceptions.TimeIntervalOccupiedException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    void setHistoryManager(HistoryManager manager);

    List<Task> getHistory();

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getSubtasksByEpicId(int id);

    List<Task> getPrioritizedTasks();

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

    void createTask(Task task) throws TimeIntervalOccupiedException;

    void createEpic(Epic epic) throws TimeIntervalOccupiedException;

    void createSubtask(Subtask subtask) throws TimeIntervalOccupiedException;

    boolean intersectsTask(Task t1, Task t2);
}
