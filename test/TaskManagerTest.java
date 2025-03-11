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
        for (int i = 0; i < 5; i++) {
            new Subtask(String.format("Subtask 1.#%d", i), "None", epic);
        }
        manager.createEpic(epic);

        Epic epic2 = new Epic("epic 2", "none");
        for (int i = 0; i < 3; i++) {
            new Subtask(String.format("Subtask 2.#%d", i), "None", epic2);
        }
        manager.createEpic(epic2);
        assertEquals(manager.getAllEpics().size(), 2);
        assertEquals(manager.getAllSubtasks().size(), 8);

        manager.deleteSubtaskById(2);
        manager.deleteSubtaskById(7);
        assertEquals(manager.getAllEpics().size(), 2);
        assertEquals(manager.getAllSubtasks().size(), 6);

        manager.deleteAllSubtasks();
        assertEquals(manager.getAllEpics().size(), 2);
        assertEquals(manager.getAllSubtasks().size(), 0);
    }

    @Test
    void epicsCanBeDeletedByIdAndAllOfThemCanBeCleared() {
        Epic epic = new Epic("epic", "none");
        for (int i = 0; i < 2; i++) {
            new Subtask(String.format("Subtask 1.#%d", i), "None", epic);
        }
        manager.createEpic(epic);

        Epic epic2 = new Epic("epic 2", "none");
        for (int i = 0; i < 3; i++) {
            new Subtask(String.format("Subtask 2.#%d", i), "None", epic2);
        }
        manager.createEpic(epic2);

        Epic epic3 = new Epic("epic 3", "none");
        for (int i = 0; i < 1; i++) {
            new Subtask(String.format("Subtask 3.#%d", i), "None", epic3);
        }
        manager.createEpic(epic3);
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

        assertEquals(manager.getAllTasks().size(), 5);
        manager.deleteTaskById(0);
        assertEquals(manager.getAllTasks().size(), 4);
        manager.deleteAllTasks();
        assertEquals(manager.getAllTasks().size(), 0);
    }

    @Test
    void epicStatusIsUpdatedCorrectly() {
        Epic epic = new Epic("Epic", "Test epic");
        Subtask subtask1 = new Subtask("Subtask 1", "1.1", epic);
        Subtask subtask2 = new Subtask("Subtask 2", "1.2", epic);
        manager.createEpic(epic);
        assertEquals(manager.getEpic(0).getStatus(), TaskStatus.NEW,
                "не корректный статус при создании эпика");

        subtask1.updateStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        assertEquals(manager.getEpic(0).getStatus(), TaskStatus.IN_PROGRESS,
                "не корректный статус при обновление одной из подзадач");

        manager.deleteSubtaskById(1);
        assertEquals(manager.getEpic(0).getStatus(), TaskStatus.NEW,
                "Статус не обновился до правильного при удалении одной из подзадач");
        assertEquals(manager.getEpic(0).getSubtasks().size(), 1,
                "Задача не была удалена из эпика");

        subtask2.updateStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask2);
        assertEquals(manager.getEpic(0).getStatus(), TaskStatus.DONE,
                "статус эпика не корректен при завершении всех задач");

        Subtask subtask3 = new Subtask("Subtask 3", "1.3", epic);
        manager.createSubtask(subtask3);
        assertEquals(manager.getEpic(0).getStatus(), TaskStatus.IN_PROGRESS,
                "статус эпика не обнавлен при добавлении новой задачи");

        subtask3.updateStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask3);
        assertEquals(manager.getEpic(0).getStatus(), TaskStatus.DONE,
                "статус эпика не корректен при завершении всех задач");
    }

    @Test
    void historyIsBeingTrackedCorrectly() {
        Task task = new Task("Regular", "Regular Task");
        Epic epic = new Epic("Epic", "Epic Task");
        Subtask subtask1 = new Subtask("Subtask 1", "subtask 1.1", epic);
        Subtask subtask2 = new Subtask("Subtask 2", "subtask 1.2", epic);
        manager.createTask(task);
        manager.createEpic(epic);
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

        assertEquals(task.getId(), 1);
        assertEquals(task2.getId(), 0);
        assertEquals(task3.getId(), 2);
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
        Epic epic = new Epic("epic 1", "epic");
        Subtask subtask = new Subtask("subtask 1", "sub", epic);
        Subtask anotherSubtask = new Subtask("subtask 2", "sub", epic,
                30L, LocalDateTime.now());

        manager.createTask(task);
        manager.createTask(task1);
        manager.createEpic(epic);

        List<Task> properTaskSet = new ArrayList<>(List.of(task, task1, epic, subtask, anotherSubtask));
        final List<Task> prioritizedTasks = properTaskSet.stream()
                .filter(t -> t.getStartTime() != null)
                .sorted(Comparator.comparing(Task::getStartTime))
                .toList();
        assertEquals(prioritizedTasks, manager.getPrioritizedTasks());
    }
}
