import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import util.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}