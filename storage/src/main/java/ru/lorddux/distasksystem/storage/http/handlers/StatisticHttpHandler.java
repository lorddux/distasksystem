package ru.lorddux.distasksystem.storage.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.lorddux.distasksystem.storage.Adapter;

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
