import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.TaskStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {
    Task task;

    @BeforeEach
    void beforeEach() {
        task = new Task("Task", "none");
    }

    @Test
    void twoTasksWithEqualIdAreEqual() {
        Task task = new Task("Task 1", "1");
        task.setId(0);
        Task extraTask = new Task("Task 2", "2");
        extraTask.setId(0);
        assertEquals(task, extraTask);
    }

    @Test
    void taskStatusIsUpdatedCorrectly() {
        assertEquals(task.getStatus(), TaskStatus.NEW);

        task.updateStatus(TaskStatus.IN_PROGRESS);
        assertEquals(task.getStatus(), TaskStatus.IN_PROGRESS);

        task.updateStatus(TaskStatus.DONE);
        assertEquals(task.getStatus(), TaskStatus.DONE);
    }

    @Test
    void customIdCanBeSet() {
        task.setId(8);
        assertEquals(task.getId(), 8);
        task.setId(4);
        assertEquals(task.getId(), 4);
    }

    @Test
    void taskIndicatesIntersections() {
        task = new Task(task.getName(), task.getDescription(), 30L, LocalDateTime.now());
        Task anotherTask = new Task("task", "extra", 15L, LocalDateTime.now());
        assertTrue(task.intersectsTask(anotherTask), "ошибка в проверке таска");

        Epic anotherEpic = new Epic("epic", "extra");
        Subtask anotherSubtask = new Subtask("subtask", "extra",
                anotherEpic, 15L, LocalDateTime.now());
        assertTrue(task.intersectsTask(anotherEpic), "ошибка в проверке эпика");
        assertTrue(task.intersectsTask(anotherSubtask), "ошибка в проверке сабтаска");
    }
}