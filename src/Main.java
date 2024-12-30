import tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Task 1", "Test 1");
        Task task2 = new Task("Task 2", "Test 2");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Test 3");
        Subtask subtask1 = new Subtask("Subtask 1.1", "Test 4", epic1);
        Subtask subtask2 = new Subtask("Subtask 1.2", "Test 5", epic1);
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Epic 2", "Test 6");
        Subtask subtask3 = new Subtask("Subtask 2", "Test 7", epic2);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask3);

        // обновление статуса задачи
        task1.updateStatus(TaskStatus.DONE);
        subtask1.updateStatus(TaskStatus.DONE);
        subtask3.updateStatus(TaskStatus.DONE);
        taskManager.updateTask(task1);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask3);
        taskManager.updateEpic(epic2);

        // получение задач по id
        System.out.println(taskManager.getTask(0));
        System.out.println(taskManager.getEpic(2));
        System.out.println(taskManager.getSubtask(4));
        System.out.println();

        // получение подзадач по id эпика
        System.out.println(taskManager.getSubtasksByEpicId(5));
        System.out.println();

        // полуяение задач по типам
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println();

        // удаление по id
        taskManager.deleteTaskById(0);
        taskManager.deleteEpicById(5);
        taskManager.deleteSubtaskById(3);
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        // проверка очищения задач
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics(); // с удалением всех эпиков удаляются все подзадачи
        System.out.println("Количество обычных задач: " + taskManager.getAllTasks().size());
        System.out.println("Количество эпиков: " + taskManager.getAllEpics().size());
        System.out.println("Количество подзадач задач: " + taskManager.getAllSubtasks().size());
    }
}