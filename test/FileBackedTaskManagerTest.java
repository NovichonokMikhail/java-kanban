import managers.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;


class FileBackedTaskManagerTest {


    @Test
    void loadFromFile() throws IOException {
        // создаю тестовые задания
        File tempFile = File.createTempFile("/java-kanban/", "temp.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task testTask = new Task("Task 1", "task example");
        testTask.updateStatus(TaskStatus.DONE);
        manager.createTask(testTask);

        Epic testEpic = new Epic("Epic 2", "epic example");
        manager.createEpic(testEpic);

        Subtask testSubtask1 = new Subtask("Subtask 1", "subtask example 1", testEpic);
        manager.createSubtask(testSubtask1);
        Subtask testSubtask2 = new Subtask("Subtask 2", "subtask example 2", testEpic);
        testSubtask2.updateStatus(TaskStatus.DONE);
        manager.createSubtask(testSubtask2);

        // проверяю создание файла
        assertTrue(Files.exists(tempFile.toPath()));

        // загрузка менеджера
        manager = FileBackedTaskManager.loadFromFile(tempFile);
        // проверка размерности заданий
        assertEquals(1, manager.getAllTasks().size(),
                "файл сохранил неправильно количество заданий");
        assertEquals(1, manager.getAllEpics().size(),
                "файл сохранил неправильно количество заданий");
        assertEquals(2, manager.getAllSubtasks().size(),
                "файл сохранил неправильно количество заданий");

        // проверка совпадения задач
        final Task loadedTask = manager.getTask(testTask.getId());
        assertNotNull(loadedTask, "id задач не совпадает");
        assertEquals(testTask, loadedTask, "задачи не равны");

        final Epic loadedEpic = manager.getEpic(testEpic.getId());
        assertNotNull(loadedEpic, "id эпиков не совпадает");
        assertEquals(testEpic, loadedEpic, "эпики не равны");

        final Subtask loadedSubtask1 = manager.getSubtask(testSubtask1.getId());
        assertNotNull(loadedSubtask1);
        assertEquals(testSubtask1, loadedSubtask1);

        final Subtask loadedSubtask2 = manager.getSubtask(testSubtask2.getId());
        assertNotNull(loadedSubtask2);
        assertEquals(testSubtask2, loadedSubtask2);
    }
}