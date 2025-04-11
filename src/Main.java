import managers.FileBackedTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.TaskStatus;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // создаем менеджер
        File tempFile = File.createTempFile("/java-kanban/", "temp.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task testTask = new Task("Task 1", "task example");
        testTask.updateStatus(TaskStatus.DONE);
        manager.createTask(testTask);

        Epic testEpic = new Epic("Epic 2", "epic example");
        manager.createEpic(testEpic);

        Subtask testSubtask1 = new Subtask("Subtask 1", "subtask example 1", testEpic.getId());
        manager.createSubtask(testSubtask1);
        Subtask testSubtask2 = new Subtask("Subtask 2", "subtask example 2", testEpic.getId());
        manager.createSubtask(testSubtask2);

        manager = FileBackedTaskManager.loadFromFile(tempFile);
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
    }
}
