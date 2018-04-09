package ru.hse.lorddux;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.hse.lorddux.config.Configuration;

import java.io.IOException;

public class ConfigHttpHandler extends BaseHttpHandler {

    public ConfigHttpHandler(String authorization) {
        super(authorization);
        methodName = "POST";
    }

    @Override
    protected void processGoodRequest(HttpExchange exchange) throws IOException {
        String configRaw = new String(exchange.getRequestBody().readAllBytes());
        Configuration.setInstance(
                new Gson().fromJson(configRaw, Configuration.class)
        );
        goodResponse(exchange, "OK");
    }
}
