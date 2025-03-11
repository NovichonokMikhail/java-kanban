import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
            Subtask subtask = new Subtask(String.format("Subtask #%d", i), "none",
                    epic);
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

    @Test
    void epicUpdatesStatus() {
        assertEquals(TaskStatus.NEW, epic.getStatus());
        Subtask subtask1 = new Subtask("test1", "1", epic);

        assertEquals(TaskStatus.NEW, epic.getStatus());
        subtask1.updateStatus(TaskStatus.IN_PROGRESS);
    }

    @Test
    void allSubtasksAreNew() {
        for (int i = 0; i < 4; i++) {
            new Subtask("test " + i, "test task", epic);
        }
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void allSubtasksAreDone() {
        for (int i = 0; i < 4; i++) {
            new Subtask("test " + i, "test task", epic).updateStatus(TaskStatus.DONE);
        }
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void allSubtasksAreNewOrDone() {
        for (int i = 0; i < 2; i++) {
            new Subtask("test " + i, "test task", epic).updateStatus(TaskStatus.DONE);
        }
        for (int i = 0; i < 2; i++) {
            new Subtask("test " + i, "test task", epic);
        }
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void allSubtasksAreInProgress() {
        for (int i = 0; i < 4; i++) {
            new Subtask("test " + i, "test task", epic).updateStatus(TaskStatus.IN_PROGRESS);
        }
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void taskIndicatesIntersections() {
        Subtask epicsSubtask = new Subtask("Epic's subtask", "test", epic, 50L,
                LocalDateTime.now());
        epicsSubtask.setId(0);
        assertFalse(epic.intersectsTask(epicsSubtask));

        Task anotherTask = new Task("task", "extra", 15L, LocalDateTime.now());
        assertTrue(epic.intersectsTask(anotherTask), "ошибка в проверке таска");

        Epic anotherEpic = new Epic("epic", "extra");
        Subtask anotherSubtask = new Subtask("subtask", "extra",
                anotherEpic, 15L, LocalDateTime.now());
        anotherSubtask.setId(1);
        assertTrue(epic.intersectsTask(anotherEpic), "ошибка в проверке эпика");
        assertTrue(epic.intersectsTask(anotherSubtask), "ошибка в проверке сабтаска");
    }
}