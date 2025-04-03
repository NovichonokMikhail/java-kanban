package tasksTests;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseTaskTest<T extends Task> {
    TaskManager manager;
    T task;

    public abstract T createTestTask();

    public abstract T createTestTaskWithStartTime();

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
        task = createTestTask();
        task.setId(0);
    }

    @Test
    void twoTasksWithSameIdAreEqual() {
        T task2 = createTestTask();
        task2.setId(0);
        assertEquals(task, task2);
    }

    @Test
    void taskStatusUpdatesProperly() {
        assertEquals(TaskStatus.NEW, task.getStatus());
        task.updateStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        task.updateStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void findsIntersectionsWithTask() {
        task = createTestTaskWithStartTime();

        Task taskWithNoTime = new Task("Regular task", "reg task with no time");
        assertFalse(manager.intersectsTask(taskWithNoTime,  taskWithNoTime));

        Task intersectingTask = new Task("Regular task", "reg intersecting task"
                , 15L, LocalDateTime.now());
        assertTrue(manager.intersectsTask(task, intersectingTask));

        Task nonIntersectingTask1 = new Task("Regular task",
                "reg not-intersecting task with time before actual task"
                , 15L, LocalDateTime.now().minusMinutes(30));
        assertFalse(manager.intersectsTask(task, nonIntersectingTask1));

        Task nonIntersectingTask2 = new Task("Regular task",
                "reg not-intersecting task with time after actual task"
                , 15L, LocalDateTime.now().plusMinutes(30));

        assertFalse(manager.intersectsTask(task, nonIntersectingTask2));
    }

    @Test
    void findsIntersectionsWithEpic() {
        task = createTestTaskWithStartTime();
        task.setId(0);

        Epic epicWithNoTime = new Epic("Epic task", "temp task");
        epicWithNoTime.setId(1);
        assertFalse(manager.intersectsTask(task, epicWithNoTime));

        Epic nonIntersectingEpic = new Epic("Regular task", "non-intersecting epic");
        nonIntersectingEpic.setId(12);
        Subtask nonIntersectingSubtask = new Subtask("temp", "temp subtask", nonIntersectingEpic.getId(),
                15L, LocalDateTime.now().minus(Duration.ofMinutes(30)));
        manager.createEpic(nonIntersectingEpic);
        manager.createSubtask(nonIntersectingSubtask);

        assertFalse(manager.intersectsTask(task, nonIntersectingEpic));
    }

    @Test
    void findsIntersectionsWithSubtask() {
        task = createTestTaskWithStartTime();
        manager.createEpic(new Epic("temp epic", "temp"));

        Subtask subtaskWithNoTime = new Subtask("Subtask",
                "subtask with no time", 0);
        assertFalse(manager.intersectsTask(subtaskWithNoTime, subtaskWithNoTime));

        Subtask nonIntersectingSubtask = new Subtask("Regular subtask", "non-intersecting subtask",
                0, 15L, LocalDateTime.now().minus(Duration.ofMinutes(30)));
        assertFalse(manager.intersectsTask(task, nonIntersectingSubtask));
    }
}