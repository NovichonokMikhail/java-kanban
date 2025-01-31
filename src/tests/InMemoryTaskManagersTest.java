package tests;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagersTest {

    TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
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
        assertEquals(manager.getAllEpics().size(), 3);
        assertEquals(manager.getAllSubtasks().size(), 6);

        manager.deleteEpicById(0);
        assertEquals(manager.getAllEpics().size(), 2);
        assertEquals(manager.getAllSubtasks().size(), 4);

        manager.deleteAllEpics();
        assertEquals(manager.getAllEpics().size(), 0);
        assertEquals(manager.getAllSubtasks().size(), 0);
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
        assertEquals(manager.getEpic(0).getStatus(), TaskStatus.NEW
                , "не корректный статус при создании эпика");

        subtask1.updateStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        assertEquals(manager.getEpic(0).getStatus(), TaskStatus.IN_PROGRESS
                , "не корректный статус при обновление одной из подзадач");

        manager.deleteSubtaskById(1);
        assertEquals(manager.getEpic(0).getStatus(), TaskStatus.NEW
                , "Статус не обновился до правильного при удалении одной из подзадач");
        assertEquals(manager.getEpic(0).getSubtasks().size(), 1
                , "Задача не была удалена из эпика");

        subtask2.updateStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask2);
        assertEquals(manager.getEpic(0).getStatus(), TaskStatus.DONE
                , "статус эпика не корректен при завершении всех задач");

        Subtask subtask3 = new Subtask("Subtask 3", "1.3", epic);
        manager.createSubtask(subtask3);
        assertEquals(manager.getEpic(0).getStatus(), TaskStatus.IN_PROGRESS
                , "статус эпика не обнавлен при добавлении новой задачи");

        subtask3.updateStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask3);
        assertEquals(manager.getEpic(0).getStatus(), TaskStatus.DONE
                , "статус эпика не корректен при завершении всех задач");
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
    void inMemoryTaskManagerDonTConflictWithTasksWithCustomId() {
        Task task = new Task("Regular Task", "Task");
        manager.createTask(task);
        Task task2 = new Task("Regular Task 2", "Task 2");
        task2.setId(0);
        manager.createTask(task2);

        assertNotNull(manager.getTask(1), "Кастомный id перезаписал генерирующий");
        assertEquals(manager.getTask(1), task2);
    }

    @Test
    void inMemoryTaskManagerAdjustsIdWhenEncountersTasksWithCustomId() {
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
    void inMemoryTaskManagerDoesNotChangeDataAfterAddingTask() {
        Task task = new Task("Task", "Original task");
        manager.createTask(task);

        assertEquals(task.getName(), manager.getTask(0).getName()
                , "названия не совпадают");
        assertEquals(task.getDescription(), manager.getTask(0).getDescription()
                , "описания не совпадают");
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
}