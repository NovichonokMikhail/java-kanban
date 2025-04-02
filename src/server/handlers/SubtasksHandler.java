package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskCreationError;
import managers.TaskManager;
import tasks.Subtask;
import util.TaskType;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
    public SubtasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length > 3 || path.length == 0) {
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
            try {
                List<Subtask> tasksList = manager.getAllSubtasks();
                sendResponse(exchange, gson.toJson(tasksList), 200);
            } catch (Exception e) {
                sendResponse(exchange, e.getMessage(), 500);
            }
            return;
        } else if (path.length > 3) {
            sendResponse(exchange, "Bad request", 400);
            return;
        }

        int id = parseId(path[2]);
        if (id == -1) {
            sendResponse(exchange, "could not parse id", 400);
            return;
        }

        Subtask s = manager.getSubtask(id);
        if (s != null) {
            sendResponse(exchange, gson.toJson(s), 200);
            return;
        }
        sendResponse(exchange, "Not found", 404);
    }

    @Override
    public void handlePost(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 2) {
            try {
                createTask(exchange, TaskType.SUBTASK);
            } catch (JsonSyntaxException e) {
                System.out.println("error: " + e.getMessage());
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
                createTask(exchange, TaskType.SUBTASK, id);
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
            manager.deleteAllSubtasks();
            sendResponse(exchange, "all subtasks were deleted", 200);
            return;
        } else if (path.length == 3){
            int id = parseId(path[2]);
            if (id == -1) {
                sendResponse(exchange, "Некоректный id", 400);
                return;
            }
            manager.deleteSubtaskById(id);
            sendResponse(exchange, "Successfully deleted", 200);
            return;
        }
        sendResponse(exchange, "Bad request", 400);
    }
}
