package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskCreationError;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import util.TaskType;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length > 4 || path.length == 0) {
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
            int responseCode;
            try {
                response = gson.toJson(manager.getAllEpics());
                responseCode = 200;
            } catch (Exception e) {
                response = "failed at making a json";
                responseCode = 500;
            }
            sendResponse(exchange, response, responseCode);
            return;
        } else if (path.length == 3) {
            int id = parseId(path[2]);
            if (id == -1) {
                sendResponse(exchange, "could not parse id", 400);
                return;
            }
            Epic epic = manager.getEpic(id);
            if (epic != null) {
                sendResponse(exchange, gson.toJson(epic), 200);
                return;
            }
            sendResponse(exchange, "Not found", 404);
            return;
        } else if (path.length == 4 && path[3].equals("subtasks")) {
            int id = parseId(path[2]);
            if (id == -1) {
                sendResponse(exchange, "could not parse id", 400);
                return;
            }
            Epic epic = manager.getEpic(id);
            if (epic != null) {
                List<Subtask> subtasksList = manager.getSubtasksByEpicId(id);
                sendResponse(exchange, gson.toJson(subtasksList), 200);
                return;
            }
            sendResponse(exchange, "Not found", 404);
            return;
        }
        sendResponse(exchange, "Bad request", 400);
    }

    @Override
    public void handlePost(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 2) {
            try {
                createTask(exchange, TaskType.EPIC);
                return;
            } catch (JsonSyntaxException e) {
                return;
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
                createTask(exchange, TaskType.EPIC, id);
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
        if (path.length != 2) {
            int id = parseId(path[3]);
            if (id == -1) {
                sendResponse(exchange, "Некоректный id", 400);
                return;
            }
            manager.deleteEpicById(id);
            sendResponse(exchange, "Эпик был удален", 200);
            return;
        }
        sendResponse(exchange, "Bad request", 400);
    }
}
