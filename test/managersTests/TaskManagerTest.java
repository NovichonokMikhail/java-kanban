package managersTests;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected abstract T createManger();

    @BeforeEach
    void beforeEach() {
        manager = createManger();
        manager.setHistoryManager(Managers.getDefaultHistory());
    }

    @Test
    void subtasksCanBeDeletedByIdAndAllOfThemCanBeCleared() {
        Epic epic = new Epic("epic", "none");
        manager.createEpic(epic);
        for (int i = 0; i < 5; i++) {
            Subtask s = new Subtask(String.format("Subtask 1.#%d", i), "None", epic.getId());
            manager.createSubtask(s);
        }

        Epic epic2 = new Epic("epic 2", "none");
        manager.createEpic(epic2);
        for (int i = 0; i < 3; i++) {
            Subtask s = new Subtask(String.format("Subtask 2.#%d", i), "None", epic2.getId());
            manager.createSubtask(s);
        }
        assertEquals(2, manager.getAllEpics().size());
        assertEquals(8, manager.getAllSubtasks().size());

        manager.deleteSubtaskById(2);
        manager.deleteSubtaskById(7);
        assertEquals(2, manager.getAllEpics().size());
        assertEquals(6, manager.getAllSubtasks().size());

        manager.deleteAllSubtasks();
        assertEquals(2, manager.getAllEpics().size());
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    void epicsCanBeDeletedByIdAndAllOfThemCanBeCleared() {
        Epic epic = new Epic("epic", "none");
        manager.createEpic(epic);
        for (int i = 0; i < 2; i++) {
            Subtask s = new Subtask(String.format("Subtask 1.#%d", i), "None", epic.getId());
            manager.createSubtask(s);
        }

        Epic epic2 = new Epic("epic 2", "none");
        manager.createEpic(epic2);
        for (int i = 0; i < 3; i++) {
            Subtask s = new Subtask(String.format("Subtask 2.#%d", i), "None", epic2.getId());
            manager.createSubtask(s);
        }

        Epic epic3 = new Epic("epic 3", "none");
        manager.createEpic(epic3);
        for (int i = 0; i < 1; i++) {
            Subtask s = new Subtask(String.format("Subtask 3.#%d", i), "None", epic3.getId());
            manager.createSubtask(s);
        }
        assertEquals(3, manager.getAllEpics().size());
        assertEquals(6, manager.getAllSubtasks().size());

        manager.deleteEpicById(0);
        assertEquals(2, manager.getAllEpics().size());
        assertEquals(4, manager.getAllSubtasks().size());

        manager.deleteAllEpics();
        assertEquals(0, manager.getAllEpics().size());
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    void tasksCanBeDeletedByIdAndAllOfThemCanBeCleared() {
        for (int i = 0; i < 5; i++) {
            manager.createTask(new Task(String.format("Task #%d", i), "None"));
        }

        assertEquals(5, manager.getAllTasks().size());
        manager.deleteTaskById(0);
        assertEquals(4, manager.getAllTasks().size());
        manager.deleteAllTasks();
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    void epicStatusIsUpdatedCorrectly() {
        Epic epic = new Epic("Epic", "Test epic");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "1.1", epic.getId());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "1.2", epic.getId());
        manager.createSubtask(subtask2);
        assertEquals(TaskStatus.NEW, manager.getEpic(0).getStatus(),
                "не корректный статус при создании эпика");

        subtask1.updateStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(0).getStatus(),
                "не корректный статус при обновление одной из подзадач");

        manager.deleteSubtaskById(1);
        assertEquals(TaskStatus.NEW, manager.getEpic(0).getStatus(),
                "Статус не обновился до правильного при удалении одной из подзадач");
        assertEquals(1, manager.getEpic(0).getSubtasksIds().size(),
                "Задача не была удалена из эпика");

        subtask2.updateStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask2);
        assertEquals(TaskStatus.DONE, manager.getEpic(0).getStatus(),
                "статус эпика не корректен при завершении всех задач");

        Subtask subtask3 = new Subtask("Subtask 3", "1.3", epic.getId());
        manager.createSubtask(subtask3);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(0).getStatus(),
                "статус эпика не обнавлен при добавлении новой задачи");

        subtask3.updateStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask3);
        assertEquals(TaskStatus.DONE, manager.getEpic(0).getStatus(),
                "статус эпика не корректен при завершении всех задач");
    }

    @Test
    void historyIsBeingTrackedCorrectly() {
        Task task = new Task("Regular", "Regular Task");
        manager.createTask(task);
        Epic epic = new Epic("Epic", "Epic Task");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "subtask 1.1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "subtask 1.2", epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.getSubtask(3);
        manager.getEpic(1);
        manager.getTask(0);
        manager.getSubtask(2);

        final List<Task> correctHistory = new ArrayList<>(List.of(subtask2, epic, task, subtask1));
        final List<Task> actualHistory = manager.getHistory();

        assertEquals(correctHistory, actualHistory, "история запросов не совпадает");
    }

    @Test
    void historyCantContainDuplicates() {
        Task task = new Task("Task 1", "test task");
        Task extraTask = new Task("Task 2", "additional task");

        manager.createTask(task);
        manager.createTask(extraTask);

        manager.getTask(0);
        manager.getTask(1);
        manager.getTask(0);

        final List<Task> history = manager.getHistory();
        final List<Task> correctHistory = List.of(extraTask, task);

        assertEquals(2, history.size(), "содержатся дубликаты задач");
        assertEquals(correctHistory, history);
    }

    @Test
    void taskManagerDonTConflictWithTasksWithCustomId() {
        Task task = new Task("Regular Task", "Task");
        manager.createTask(task);
        Task task2 = new Task("Regular Task 2", "Task 2");
        task2.setId(0);
        manager.createTask(task2);

        assertNotNull(manager.getTask(1), "Кастомный id перезаписал генерирующий");
        assertEquals(manager.getTask(1), task2);
    }

    @Test
    void taskManagerAdjustsIdWhenEncountersTasksWithCustomId() {
        Task task = new Task("Regular Task 1", "Task 1");
        task.setId(1);
        manager.createTask(task);
        Task task2 = new Task("Regular Task 2", "Task 2");
        manager.createTask(task2);
        Task task3 = new Task("Regular Task 3", "Task 3");
        manager.createTask(task3);

        assertEquals(1, task.getId());
        assertEquals(0, task2.getId());
        assertEquals(2, task3.getId());
    }

    @Test
    void taskManagerDoesNotChangeDataAfterAddingTask() {
        Task task = new Task("Task", "Original task");
        manager.createTask(task);

        assertEquals(task.getName(), manager.getTask(0).getName(),
                "названия не совпадают");
        assertEquals(task.getDescription(), manager.getTask(0).getDescription(),
                "описания не совпадают");
    }

    @Test
    void historyManagerContainsPreviousVersionOfATaskAndData() {
        Task task = new Task("Go shopping", "buy milk");
        manager.createTask(task);
        manager.getTask(0);
        task.updateStatus(TaskStatus.DONE);
        manager.updateTask(task);
        final Task previousTaskVersion = manager.getHistory().getFirst();

        assertEquals(previousTaskVersion, manager.getTask(0), "обновленная версия совпадает со старой");
    }

    @Test
    void prioritizedAreWorkingAsIntended() {
        Task task = new Task("task 1", "regular", 15L
                , LocalDateTime.of(2025, 10, 3, 12, 50));
        Task task1 = new Task("task 2", "regular", 30L
                , LocalDateTime.of(2025, 10, 3, 12, 10));
        manager.createTask(task);
        manager.createTask(task1);

        Epic epic = new Epic("epic 1", "epic");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("subtask 1", "sub", epic.getId());
        Subtask anotherSubtask = new Subtask("subtask 2", "sub", epic.getId(),
                30L, LocalDateTime.now());

        manager.createSubtask(subtask);
        manager.createSubtask(anotherSubtask);

        List<Task> properTaskSet = new ArrayList<>(List.of(task, task1, epic, subtask, anotherSubtask));
        final List<Task> prioritizedTasks = properTaskSet.stream()
                .filter(t -> t.getStartTime() != null)
                .sorted(Comparator.comparing(Task::getStartTime))
                .toList();
        assertEquals(prioritizedTasks, manager.getPrioritizedTasks());
    }
}
