package ru.hse.lorddux;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.hse.lorddux.config.ConfigurationClient;
import ru.hse.lorddux.executor.PythonExecutor;
import ru.hse.lorddux.data.request.PCPropertiesData;

import java.util.List;

/**
 * the main class if the worker
 * obtains configuration.
 * starts up executors, queue processors
 */
@RequiredArgsConstructor
public class Adapter implements Runnable{
    private static Logger log_ = LogManager.getLogger(Adapter.class);

    @NonNull private String configServerHost;
    @NonNull private String configPath;
    @NonNull private Long sleepTime;
    private volatile boolean stop = false;
    private List<PythonExecutor> executors;

    @Override
    public void run() {
        ConfigurationClient configurationClient = new ConfigurationClient(
                "google.com", "", new PCPropertiesData()
        );
        Thread kk = new Thread(configurationClient);
        try {
            kk.start();
            kk.join();
        } catch (InterruptedException e) {

        }
    }

    public static void main(String[] args) throws Exception {
        Thread configThread = new Thread(new Adapter("", "", 1234L));
        configThread.start();
        configThread.join();
    }
}
