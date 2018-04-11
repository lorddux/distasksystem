package ru.lorddux.distasksystem.worker.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;

@RequiredArgsConstructor
public abstract class BaseHttpHandler implements HttpHandler {
    private static Logger log_ = LogManager.getLogger(BaseHttpHandler.class);
    private static final String BAD_METHOD_MESSAGE = "%s method is not supported";
    private static final String BAD_AUTH_MESSAGE = "Bad authorization: %s";

    @NonNull
    private String authorization;
    protected String methodName;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        log_.info(String.format("Received request from %s", exchange.getRemoteAddress()));
        String requestMethod = exchange.getRequestMethod();
        String authorization = exchange.getRequestHeaders().get("x-auth").get(0);
        if (requestMethod.equalsIgnoreCase(methodName)) {
            if (authorization.equals(this.authorization)) {
                processGoodRequest(exchange);
            } else {
                String errorMessage = String.format(BAD_AUTH_MESSAGE, authorization);
                errorResponse(exchange, errorMessage);
            }
        } else {
            String errorMessage = String.format(BAD_METHOD_MESSAGE, requestMethod);
            errorResponse(exchange, errorMessage);
        }
        exchange.close();
    }

    protected abstract void processGoodRequest(HttpExchange exchange) throws IOException;

    protected void goodResponse(HttpExchange exchange, String message) throws IOException {
        response(exchange, message, 200);
    }

    private void errorResponse(HttpExchange exchange, String errorMessage) throws IOException{
        response(exchange, errorMessage, 400);
    }

    private void response(HttpExchange exchange, String message, int rCode) throws IOException {
        exchange.sendResponseHeaders(rCode, message.length());
        OutputStream body = exchange.getResponseBody();
        body.write(message.getBytes("UTF-8"));
        body.close();
    }

}
