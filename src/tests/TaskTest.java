package tests;

import org.junit.jupiter.api.BeforeEach;
import tasks.Task;

import org.junit.jupiter.api.Test;
import util.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    Task task;

    @BeforeEach
    void beforeEach() {
        task = new Task("Task", "none");
    }

    @Test
    void TwoTasksWithEqualIdAreEqual() {
        Task task = new Task("Task 1", "1");
        task.setId(0);
        Task extraTask = new Task("Task 2", "2");
        extraTask.setId(0);
        assertEquals(task, extraTask);
    }

    @Test
    void TaskStatusIsUpdatedCorrectly() {
        assertEquals(task.getStatus(), TaskStatus.NEW);

        task.updateStatus(TaskStatus.IN_PROGRESS);
        assertEquals(task.getStatus(), TaskStatus.IN_PROGRESS);

        task.updateStatus(TaskStatus.DONE);
        assertEquals(task.getStatus(), TaskStatus.DONE);
    }

    @Test
    void CustomIdCanBeSet() {
        task.setId(8);
        assertEquals(task.getId(), 8);
        task.setId(4);
        assertEquals(task.getId(), 4);
    }
}