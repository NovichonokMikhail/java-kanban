package tasksTests;

import tasks.Subtask;

import java.time.LocalDateTime;

class SubtaskTest extends BaseTaskTest<Subtask> {
    @Override
    public Subtask createTestTask() {
        return new Subtask("Subtask", "test scenario subtask", 1);
    }

    @Override
    public Subtask createTestTaskWithStartTime() {
        return new Subtask("Subtask", "test scenario subtask", 1,
                30L, LocalDateTime.now());
    }
}