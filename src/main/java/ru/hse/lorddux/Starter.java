package ru.hse.lorddux;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.hse.lorddux.config.Configuration;
import ru.hse.lorddux.config.ConfigurationClient;
import ru.hse.lorddux.data.request.PCParametersData;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class Starter implements Service {
    private static final Logger log_ = LogManager.getLogger(Starter.class);
    private static int DEFAULT_PORT = 5151;

    @Getter
    @Setter
    @Parameter(names={"-h", "--host"}, required = true)
    private String configHost;

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void stop() {

    }

    @Override
    public void start() throws Exception {

    }

    public static void main(String[] args) throws Exception {
        Starter starter = new Starter();
        JCommander commander = new JCommander(starter);
        commander.parse(args);
        PCParametersData requestData = new PCParametersData();
        ConfigurationClient configClient = new ConfigurationClient(starter.getConfigHost(), requestData);
        Thread configThread = new Thread(configClient);
        configThread.start();
        try {
            configThread.join();
        } catch (InterruptedException e) {
            log_.error("Configuration obtaining was stopped due to error", e);
            return;
        }
        Adapter adapter = new Adapter();

        HttpsServer server = HttpsServer.create();
        server.bind(new InetSocketAddress(DEFAULT_PORT), 0);
        server.createContext("/start",
                new StartServicesHttpHandler(Configuration.getInstance().getAuthorization(), adapter)
        );
        server.createContext("/stop",
                new StopServicesHttpHandler(Configuration.getInstance().getAuthorization(), adapter)
        );
        server.createContext("/config",
                new ConfigHttpHandler(Configuration.getInstance().getAuthorization())
        );
        server.start();
    }
}
