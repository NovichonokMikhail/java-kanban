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

    static boolean intersectsTask(Task t1, Task t2) {
        if (t1 instanceof Epic e && t2 instanceof Subtask s) {
            if (e.getId() == (s.getRelatedEpicId())) return false;

        } else if (t1 instanceof Subtask s && t2 instanceof Epic e) {
            if (e.getId() == s.getRelatedEpicId()) return false;

        } else if (t1 instanceof Subtask s1 && t2 instanceof Subtask s2) {
            if (s1.getRelatedEpicId() == s2.getRelatedEpicId()) return false;

        }

        if (t1.getStartTime() != null && t2.getStartTime() != null)
            return (t1.getEndTime().isAfter(t2.getStartTime()) && t2.getStartTime().isAfter(t1.getStartTime())) ||
                    (t2.getEndTime().isAfter(t1.getStartTime()) && t1.getStartTime().isAfter(t2.getStartTime()));
        return false;
    }
}
