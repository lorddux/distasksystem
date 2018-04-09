package ru.hse.lorddux;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.hse.lorddux.config.Configuration;
import ru.hse.lorddux.config.ConfigurationClient;
import ru.hse.lorddux.data.request.PCParametersData;

public final class Starter implements Service {
    private static final Logger log_ = LogManager.getLogger(Starter.class);

    @Getter
    @Setter
    @Parameter(names={"-h", "--host"}, required = true)
    private String configHost;

    @Override
    public void stop() {

    }

    @Override
    public void start() throws Exception {

    }

    public static void main(String[] args) {
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
        Adapter adapter = new Adapter(Configuration.getInstance());
        adapter.start();
    }
}
