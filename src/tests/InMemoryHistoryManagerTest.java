package tests;

import java.util.List;

import managers.HistoryManager;
import managers.Managers;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import tasks.Task;

class InMemoryHistoryManagerTest {

    @Test
    void InMemoryHistoryManagerAddsTasksAndReturnsCorrectHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task = new Task("Task", "test task");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
        assertEquals(history.get(0), task, "Задача совпадает с изначальной копией");
    }
}