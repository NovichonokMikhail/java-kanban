package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    Epic epic;

    @BeforeEach
    void beforeEach() {
        epic = new Epic("epic", "none");
    }

    @Test
    void twoEpicsWithEqualIdAreEqual() {
        epic.setId(3);
        Epic extraEpic = new Epic("task 2", "second epic task");
        extraEpic.setId(3);
        assertEquals(epic, extraEpic);
    }

    @Test
    void methodGetSubtasksReturnCorrectListOfSubtasks() {
        ArrayList<Subtask> correctList = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            Subtask subtask = new Subtask(String.format("Subtask #%d", i), "none", epic);
            correctList.add(subtask);
        }
        assertEquals(epic.getSubtasks().size(), correctList.size());
        assertEquals(epic.getSubtasks(), correctList);
    }

    @Test
    void removeSubtaskRemovesSubtasksAsIntended() {
        Subtask subtask = new Subtask("Subtask", "1", epic);
        Subtask subtask2 = new Subtask("Subtask", "2", epic);
        assertEquals(epic.getSubtasks().size(), 2);
        epic.removeSubtask(subtask);
        assertEquals(epic.getSubtasks().size(), 1);
        epic.removeSubtask(subtask2);
        assertEquals(epic.getSubtasks().size(), 0);
    }
}