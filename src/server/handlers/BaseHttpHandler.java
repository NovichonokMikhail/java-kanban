package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskCreationError;
import exceptions.TimeIntervalOccupiedException;
import managers.TaskManager;
import server.HttpTaskServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.TaskType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager manager;
    protected final Gson gson;

    protected BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = HttpTaskServer.getStandardGson();
    }

    public static void sendResponse(HttpExchange exchange, String responseText, int responseCode) {
        try {
            byte[] response = responseText.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(responseCode, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } catch (IOException e) {
            System.out.println("Failed sending the response");
            System.out.println(e.getMessage());
            System.out.println(responseText);
            System.out.println(responseCode);
            System.out.println();
        }
    }

    public abstract void handle(HttpExchange exchange) throws IOException;

    public abstract void handleGet(HttpExchange exchange);

    public abstract void handlePost(HttpExchange exchange) throws IOException;

    public abstract void handleDelete(HttpExchange exchange);

    protected static int parseId(String strId) {
        try {
            return Integer.parseInt(strId);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    protected final void createTask(HttpExchange exchange, TaskType type) throws IOException, TaskCreationError {
        createTask(exchange, type, null);
    }

    protected final void createTask(HttpExchange exchange, TaskType type, Integer id) throws IOException, TaskCreationError {
        try {
            final String rBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            id = (id != null && id > 0) ? id : null;
            switch (type) {
                case TASK:
                    Task task = gson.fromJson(rBody, Task.class);
                    if (id != null) {
                        task.setId(id);
                        manager.updateTask(task);
                    } else {
                        manager.createTask(task);
                    }
                    sendResponse(exchange, "Задача успешно создана", 201);
                    break;
                case EPIC:
                    Epic epic = gson.fromJson(rBody, Epic.class);
                    if (id != null) {
                        epic.setId(id);
                        manager.updateEpic(epic);
                    } else {
                        manager.createEpic(epic);
                    }
                    sendResponse(exchange, "Задача успешно создана", 201);
                    break;
                case SUBTASK:
                    Subtask subtask = gson.fromJson(rBody, Subtask.class);
                    if (id != null) {
                        subtask.setId(id);
                        manager.updateSubtask(subtask);
                    } else {
                        manager.createSubtask(subtask);
                    }
                    sendResponse(exchange, "Задача успешно создана", 201);
                    break;
            }
        } catch (TimeIntervalOccupiedException e) {
            sendResponse(exchange, e.getMessage(), 406);
        } catch (Exception e) {
            throw new TaskCreationError("Task could not be created because: " + e.getMessage());
        }
    }
}
