package ru.hse.lorddux;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class StopServicesHttpHandler extends BaseHttpHandler {
    private Adapter adapter;

    public StopServicesHttpHandler(String authorization, Adapter adapter) {
        super(authorization);
        methodName = "POST";
        this.adapter = adapter;
    }

    @Override
    protected void processGoodRequest(HttpExchange exchange) throws IOException {
        if (adapter.isRunning()) {
            adapter.stop();
            goodResponse(exchange, "OK");
        } else {
            goodResponse(exchange, "NOT_OK");
        }
    }
}
