package ru.lorddux.distasksystem.storage;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.storage.config.Configuration;
import ru.lorddux.distasksystem.storage.config.ConfigurationClient;
import ru.lorddux.distasksystem.storage.data.request.PCParametersData;
import ru.lorddux.distasksystem.storage.http.handlers.ConfigHttpHandler;
import ru.lorddux.distasksystem.storage.http.handlers.StartServicesHttpHandler;
import ru.lorddux.distasksystem.storage.http.handlers.StopServicesHttpHandler;

import java.net.InetSocketAddress;

public final class Starter {
    private static final Logger log_ = LogManager.getLogger(Starter.class);
    private static int DEFAULT_PORT = 5151;

    @Getter
    @Setter
    @Parameter(names={"-h", "--host"}, required = true)
    private String configHost;

    @Getter
    @Setter
    @Parameter(names={"-p", "--port"})
    private Integer port;

    public static void main(String[] args) throws Exception {
        Starter starter = new Starter();
        JCommander commander = new JCommander(starter);
        commander.parse(args);
        ConfigurationClient configClient = new ConfigurationClient(starter.getConfigHost(), starter.getPort());
        Thread configThread = new Thread(configClient);
        configThread.start();
        try {
            configThread.join();
        } catch (InterruptedException e) {
            log_.error("Configuration obtaining was stopped due to error", e);
            return;
        }
        Adapter adapter = new Adapter();

        HttpServer server = HttpServer.create();
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
