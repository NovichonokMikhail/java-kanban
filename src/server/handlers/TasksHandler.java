package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskCreationError;
import managers.TaskManager;
import tasks.Task;
import util.TaskType;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length > 3) {
            sendResponse(exchange, "Некоректный запрос", 400);
            return;
        }

        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                sendResponse(exchange, "Метод не реализован", 501);
        }
    }

    @Override
    public void handleGet(HttpExchange exchange) {
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 2) {
            String response;
            int rCode;
            try {
                response = gson.toJson(manager.getAllTasks());
                rCode = 200;
            } catch (Exception e) {
                response = "fail making a json";
                rCode = 500;
            }
            sendResponse(exchange, response, rCode);
            return;
        } else if (path.length > 3 || path.length < 2) {
            sendResponse(exchange, "Bad request", 400);
            return;
        }

        int id = parseId(path[2]);
        if (id == -1) {
            sendResponse(exchange, "could not parse id", 400);
            return;
        }

        Task task = manager.getTask(id);
        if (task != null) {
            sendResponse(exchange, gson.toJson(task), 200);
            return;
        }
        sendResponse(exchange, "Not found", 404);
    }

    @Override
    public void handlePost(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 2) {
            try {
               createTask(exchange, TaskType.TASK);
            } catch (JsonSyntaxException e) {
                System.out.println("error");
            } catch (TaskCreationError e) {
                sendResponse(exchange, e.getMessage(), 500);
            }
           return;
        }

        if (path.length == 3) {
            int id = parseId(path[2]);
            if (id == -1) {
                sendResponse(exchange, "could not parse id", 400);
                return;
            }
            try {
                createTask(exchange, TaskType.TASK, id);
            } catch (TaskCreationError e) {
                sendResponse(exchange, e.getMessage(), 500);
            }
            return;
        }

        sendResponse(exchange, "Bad request", 400);
    }

    @Override
    public void handleDelete(HttpExchange exchange) {
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 2) {
            manager.deleteAllTasks();
            sendResponse(exchange, "all tasks had been deleted", 200);
            return;
        } else if (path.length == 3) {
            int id = parseId(path[2]);
            if (id == -1) {
                sendResponse(exchange, "Некоректный id", 400);
                return;
            }
            try {
                manager.deleteTaskById(id);
                sendResponse(exchange, "success", 200);
            } catch (Exception e) {
                sendResponse(exchange, "Internal processing error", 500);
            }
            return;
        }
        sendResponse(exchange, "Bad request", 400);
    }
}
