package tasksTests;

import tasks.Task;

import java.time.LocalDateTime;

class TaskTest extends BaseTaskTest<Task> {

    @Override
    public Task createTestTask() {
        return new Task("Regular Test Task", "reg task for test scenario");
    }

    @Override
    public Task createTestTaskWithStartTime() {
        return new Task("Regular Test Task", "reg task with time for test scenario",
                30L, LocalDateTime.now());
    }
}