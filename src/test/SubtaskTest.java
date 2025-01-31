package test;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    @Test
    void twoSubtasksWithEqualIdAreEqual() {
        Epic epic = new Epic("Epic 1", "Epic Test");
        Subtask subtask = new Subtask("Task 1", "1", epic);
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
}