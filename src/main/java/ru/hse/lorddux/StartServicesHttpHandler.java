package ru.hse.lorddux;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class StartServicesHttpHandler extends BaseHttpHandler {

    private Adapter adapter;

    public StartServicesHttpHandler(String authorization, Adapter adapter) {
        super(authorization);
        methodName = "POST";
        this.adapter = adapter;
    }

    @Override
    protected void processGoodRequest(HttpExchange exchange) throws IOException {
        if (! adapter.isRunning()) {
            adapter.start();
            goodResponse(exchange, "OK");
        } else {
            goodResponse(exchange, "NOT_OK");
        }
    }
}
