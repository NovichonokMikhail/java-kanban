package tasksTests;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import util.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest extends BaseTaskTest<Epic> {
    @Override
    public Epic createTestTask() {
        Epic e = new Epic("Epic task", "test epic object");
        e.setId(0);
        return e;
    }

    @Override
    public Epic createTestTaskWithStartTime() {
        Epic e = new Epic("Epic task", "test epic object");
        manager.createEpic(e);
        manager.createSubtask(new Subtask("temp", "time temp"
                , 0, 30L, LocalDateTime.now()));
        return manager.getEpic(0);
    }

    @Test
    void methodGetSubtasksReturnCorrectListOfSubtasks() {
        ArrayList<Integer> correctList = new ArrayList<>(5);
        manager.createEpic(task);
        for (int i = 0; i < 5; i++) {
            Subtask subtask = new Subtask(String.format("Subtask #%d", i), "none",
                    task.getId());
            manager.createSubtask(subtask);
            correctList.add(subtask.getId());
        }
        assertEquals(task.getSubtasksIds().size(), correctList.size());
        assertEquals(task.getSubtasksIds(), correctList);
    }

    @Test
    void removeSubtaskRemovesSubtasksAsIntended() {
        manager.createEpic(task);
        Subtask subtask = new Subtask("Subtask", "1", task.getId());
        Subtask subtask2 = new Subtask("Subtask", "2", task.getId());
        manager.createSubtask(subtask);
        manager.createSubtask(subtask2);

        assertEquals(2, task.getSubtasksIds().size());
        manager.deleteSubtaskById(subtask.getId());
        assertEquals(1, task.getSubtasksIds().size());
        manager.deleteSubtaskById(subtask2.getId());
        assertEquals(0, task.getSubtasksIds().size());
    }


    @Test @Override
    void taskStatusUpdatesProperly() {
        // основной тест проводится в тесте менеджера
        manager.createEpic(task);
        assertEquals(TaskStatus.NEW, task.getStatus());
        Subtask subtask1 = new Subtask("test1", "1", task.getId());
        subtask1.updateStatus(TaskStatus.DONE);
        manager.createSubtask(subtask1);
        assertEquals(TaskStatus.DONE, task.getStatus());
    }
}