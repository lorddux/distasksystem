package ru.lorddux.distasksystem.worker;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.worker.config.Configuration;
import ru.lorddux.distasksystem.worker.config.ConfigurationClient;
import ru.lorddux.distasksystem.worker.data.request.PCParametersData;
import ru.lorddux.distasksystem.worker.http.handlers.ConfigHttpHandler;
import ru.lorddux.distasksystem.worker.http.handlers.StartServicesHttpHandler;
import ru.lorddux.distasksystem.worker.http.handlers.StopServicesHttpHandler;

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
    @Parameter(names = {"-lp", "-local_port"})
    private Integer port;

    @Getter
    @Setter
    @Parameter(names = {"-lh", "--local_hostname"}, required = true)
    private String hostname;

    public static void main(String[] args) throws Exception {
        Starter starter = new Starter();
        JCommander commander = new JCommander(starter);
        commander.parse(args);
        Integer port = starter.getPort() != null ? starter.getPort() : DEFAULT_PORT;
        InetSocketAddress address = new InetSocketAddress(starter.getHostname(), port);
        HttpServer server = HttpServer.create();
        server.bind(address, 0);

        PCParametersData requestData = new PCParametersData();
        requestData.setApiAddress(String.format("%s:%s", starter.getHostname(), port));
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
