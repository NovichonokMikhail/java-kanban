import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void beforeEach() {
        epic = new Epic("Epic 1", "Epic Test");
        subtask = new Subtask("Task 1", "1", epic);
    }

    @Test
    void twoSubtasksWithEqualIdAreEqual() {
        subtask.setId(0);
        Subtask extraSubtask = new Subtask("Task 2", "2", epic);
        extraSubtask.setId(0);
        assertEquals(subtask, extraSubtask);
    }

    @Test
    void relatedEpicIsStoredCorrectly() {
        Epic epic = new Epic("Epic 1", "Epic Test");
        Subtask subtask = new Subtask("Task 1", "1", epic);
        assertEquals(epic, subtask.getRelatedEpic());
    }

    @Test
    void taskIndicatesIntersections() {
        epic.setId(5);
        subtask = new Subtask("Task 1", "1", epic, 50L,
                LocalDateTime.now());
        subtask.setId(0);
        assertFalse(subtask.intersectsTask(epic));

        Task anotherTask = new Task("task", "extra", 15L, LocalDateTime.now());
        assertTrue(subtask.intersectsTask(anotherTask), "ошибка в проверке таска");

        Epic anotherEpic = new Epic("epic", "extra");
        anotherEpic.setId(15);
        Subtask anotherSubtask = new Subtask("subtask", "extra",
                anotherEpic, 15L, LocalDateTime.now());
        anotherSubtask.setId(1);
        assertTrue(subtask.intersectsTask(anotherEpic), "ошибка в проверке эпика");
        assertTrue(subtask.intersectsTask(anotherSubtask), "ошибка в проверке сабтаска");
    }
}