package tests;

import java.util.List;

import managers.HistoryManager;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import tasks.Task;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    Task mainTask;
    Task secondaryTask;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        mainTask = new Task("Main", "test task");
        mainTask.setId(9);
        secondaryTask = new Task("Extra", "secondary test task");
        secondaryTask.setId(3);
        historyManager.add(mainTask);
        historyManager.add(secondaryTask);
    }

    @Test
    void InMemoryHistoryManagerAddsTasksAndReturnsCorrectHistory() {
        final List<Task> history = historyManager.getHistory();

        assertFalse(history.isEmpty()
                , "После добавления задачи, история не должна быть пустой.");

        assertEquals(2, history.size()
                , "После добавления 2 задач, размер должен быть 2.");
        assertEquals(history.getFirst(), mainTask, "Задача совпадает с изначальной копией");
    }

    @Test
    void InMemoryHistoryManagerDoesNotContainDuplicates() {
        historyManager.add(mainTask);
        final List<Task> history = historyManager.getHistory();
        final List<Task> correctHistory = List.of(secondaryTask, mainTask);

        assertEquals(2, history.size()
                , "история содержит дубликаты.");
        assertEquals(history.getFirst(), secondaryTask, "Задача совпадает с изначальной копией");
        assertEquals(correctHistory, history, "некоректный порядок хранения задач");
    }

    @Test
    void InMemoryHistoryManagerCanRemoveVariation1() {
        historyManager.remove(3);
        final List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size()
                , "задача не удалилась из истории");
        assertEquals(history.getFirst(), mainTask, "Задача совпадает с изначальной копией");
    }

    @Test
    void InMemoryHistoryManagerCanRemoveVariation2() {
        historyManager.remove(9);
        final List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size()
                , "задача не удалилась из истории");
        assertEquals(history.getFirst(), secondaryTask, "Задача совпадает с изначальной копией");
    }
}