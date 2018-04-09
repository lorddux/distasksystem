package ru.hse.lorddux;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@AllArgsConstructor
public class StartServicesHttpHandler implements HttpHandler {
    private static Logger log_ = LogManager.getLogger(StartServicesHttpHandler.class);
    private static final String BAD_METHOD_MESSAGE = "%s method is not supported";
    private Adapter adapter;
    private String authorization;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        log_.info(String.format("Received "));
        String requestMethod = exchange.getRequestMethod();
        String authorization = exchange.getRequestHeaders().get("x-auth").get(0);
        if (requestMethod.equalsIgnoreCase("POST")) {
            if (authorization.equals(this.authorization)) {
                if (!adapter.isRunning()) {
                    adapter.start();
                    exchange.sendResponseHeaders(200, 0);
                }
            }
        } else {
            exchange.sendResponseHeaders(404, 0);
            
        }
    }
}
