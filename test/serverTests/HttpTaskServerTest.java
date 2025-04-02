package serverTests;

import com.google.gson.Gson;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.typeTokens.TaskListTypeToken;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    final String standard_path = "http://localhost:8080/";
    final Gson gson = HttpTaskServer.getStandardGson();
    TaskManager manager = Managers.getDefault();
    HttpTaskServer server;

    @BeforeEach
    void beforeEach() throws IOException {
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    void afterEach() {
        server.stop();
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Regular task", "Testing task", 5L, LocalDateTime.now());
        String jsonTask = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(standard_path + "tasks");
        HttpRequest request = HttpRequest.newBuilder(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertFalse(tasksFromManager.isEmpty(), "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Regular task", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test #2", "epic task");
        String jsonEpic = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(standard_path + "epics");
        HttpRequest request = HttpRequest.newBuilder(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertFalse(tasksFromManager.isEmpty(), "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test #2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void testAddEpicAndSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test #2", "epic task");
        String jsonEpic = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(standard_path + "epics");
        HttpRequest request = HttpRequest.newBuilder(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertFalse(tasksFromManager.isEmpty(), "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test #2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");

        Subtask subtask = new Subtask("Testing subtask", "experimental", 0, 15L,
                LocalDateTime.now());
        String jsonSubtask = gson.toJson(subtask);

        url = URI.create(standard_path + "subtasks");
        HttpRequest request2 = HttpRequest.newBuilder(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertFalse(subtasksFromManager.isEmpty(), "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Testing subtask", subtasksFromManager.getFirst().getName()
                , "Некорректное имя задачи");

        assertFalse(manager.getEpic(0).getSubtasksIds().isEmpty());
    }

    @Test
    void taskCanBeCreatedWithCustomId() throws IOException, InterruptedException {
        Task task = new Task("Regular task", "Testing task", 5L, LocalDateTime.now());
        String jsonTask = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(standard_path + "tasks/3");
        HttpRequest request = HttpRequest.newBuilder(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertFalse(tasksFromManager.isEmpty(), "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Regular task", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        assertEquals(3, tasksFromManager.getFirst().getId(), "не устанавливается свой id");
    }

    @Test
    void canGetTasks() throws IOException, InterruptedException {
        for (int i = 0; i < 5; i++) {
            manager.createTask(new Task("Test #" + i, "experimental"));
        }

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(standard_path + "tasks");
        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> responseTasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(5, responseTasks.size());
    }

    @Test
    void canGetTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url;
        HttpRequest request;
        HttpResponse<String> response;
        Task responseTask;

        Task task1 = new Task("Test #1", "without custom id");
        manager.createTask(task1);
        Task task2 = new Task("Test #2", "with custom id");
        task2.setId(11);
        manager.createTask(task2);

        url = URI.create(standard_path + "tasks/0");
        request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        responseTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(responseTask);
        assertEquals(task1, responseTask);

        url = URI.create(standard_path + "tasks/11");
        request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        responseTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(responseTask);
        assertEquals(task2, responseTask);

        url = URI.create(standard_path + "tasks");
        request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> responseTasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(2, responseTasks.size());
        assertEquals(responseTasks, List.of(task1, task2));
    }

    @Test
    void tasksCanBeDeleted() throws IOException, InterruptedException {
        HttpClient client;
        URI url;
        HttpRequest request;
        HttpResponse<String> response;

        for (int i = 0; i < 5; i++) {
            manager.createTask(new Task("Test #" + i, "experimental"));
        }

        client = HttpClient.newHttpClient();
        url = URI.create(standard_path + "tasks/0");
        request = HttpRequest.newBuilder(url)
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(4, manager.getAllTasks().size());

        client = HttpClient.newHttpClient();
        url = URI.create(standard_path + "tasks/4");
        request = HttpRequest.newBuilder(url)
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(3, manager.getAllTasks().size());

        client = HttpClient.newHttpClient();
        url = URI.create(standard_path + "tasks");
        request = HttpRequest.newBuilder(url)
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    void canGetHistory() throws IOException, InterruptedException {
        for (int i = 0; i < 5; i++) {
            manager.createTask(new Task("Test #" + i, "experimental"));
        }

        List<Integer> historyOrder = List.of(3, 4, 0, 1, 2);
        historyOrder.forEach(manager::getTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(standard_path + "history");
        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> responseHistory = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertNotNull(responseHistory);
        assertEquals(5, responseHistory.size());
        List<Integer> responseHistoryIds = responseHistory.stream()
                .map(Task::getId)
                .toList();

        assertEquals(historyOrder, responseHistoryIds);
    }

    @Test
    void canGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("task 1", "regular", 15L
                , LocalDateTime.of(2025, 10, 3, 12, 50));
        Task task1 = new Task("task 2", "regular", 30L
                , LocalDateTime.of(2025, 10, 3, 12, 10));
        manager.createTask(task);
        manager.createTask(task1);

        Epic epic = new Epic("epic 1", "epic");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("subtask 1", "sub", epic.getId());
        Subtask anotherSubtask = new Subtask("subtask 2", "sub", epic.getId(),
                30L, LocalDateTime.now());

        manager.createSubtask(subtask);
        manager.createSubtask(anotherSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(standard_path + "prioritized");
        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> responseTasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertNotNull(responseTasks);
        assertEquals(3, responseTasks.size());
    }

    @Test
    void error404IfTriesToGetNonexistentTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(standard_path + "tasks/15");
        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void error404IfTryToGetSubtasksFromNonexistentEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(standard_path + "epics/15/subtasks");
        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}