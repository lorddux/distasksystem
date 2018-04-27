package ru.lorddux.distasksystem.worker.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.lorddux.distasksystem.worker.Adapter;

import java.io.IOException;

public class StatisticHttpHandler extends BaseHttpHandler {

    private Adapter adapter;

    public StatisticHttpHandler(String authorization) {
        super(authorization);
        methodName = "POST";
    }

    @Override
    protected void processGoodRequest(HttpExchange exchange) throws IOException {
        goodResponse(exchange, String.valueOf(adapter.getStat()));
    }
}
