package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import server.HttpTaskServer;

public class PrioritizedHandler implements HttpHandler {
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) {
        final Gson gson = HttpTaskServer.getStandardGson();
        final String method = exchange.getRequestMethod();
        final String[] path = exchange.getRequestURI().getPath().split("/");

        if (method.equals("GET") && path.length == 2) {
            BaseHttpHandler.sendResponse(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
        } else if (path.length == 2) {
            BaseHttpHandler.sendResponse(exchange, "this method does not exist", 400);
        } else {
            BaseHttpHandler.sendResponse(exchange, "this endpoint does not exist", 404);
        }
    }
}