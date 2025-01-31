import managers.*;
import tasks.*;

public class Main {
    public static void main(String[] args) {
        // создаем менеджер
        TaskManager manager = Managers.getDefault();
        // ставим менеджер для истории запросов
        manager.setHistoryManager(Managers.getDefaultHistory());

        // создаю 2 обычные задачи
        Task task1 = new Task("Task 1", "regular task");
        manager.createTask(task1);
        Task task2 = new Task("Task 2", "regular task");
        manager.createTask(task2);

        // создаю эпик
        Epic epic1 = new Epic("Epic 1", "filled epic");
        for (int i = 1; i < 4; i++) {
            // заполняю эпик подзадачами
            new Subtask(String.format("Subtask 1.%d", i), "empty", epic1);
        }
        manager.createEpic(epic1);

        // создаю второй эпик который оставлю пустым
        Epic epic2 = new Epic("Epic 2", "empty epic");
        manager.createEpic(epic2);


        // Тестовый сценарий
        manager.getTask(0);
        System.out.println(manager.getHistory());

        manager.getSubtask(4);
        System.out.println(manager.getHistory());

        manager.getSubtask(5);
        System.out.println(manager.getHistory());

        manager.getTask(0);
        System.out.println(manager.getHistory());

        manager.deleteSubtaskById(4);
        System.out.println(manager.getHistory());

        manager.getEpic(6);
        System.out.println(manager.getHistory());

        manager.getEpic(2);
        System.out.println(manager.getHistory());

        manager.deleteEpicById(2);
        System.out.println(manager.getHistory());

        manager.deleteEpicById(6);
        System.out.println(manager.getHistory());
    }
}
