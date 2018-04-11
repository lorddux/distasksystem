package ru.hse.lorddux;

import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.hse.lorddux.config.Configuration;
import ru.hse.lorddux.connector.StorageLayerConnector;
import ru.hse.lorddux.connector.StorageLayerConnectorImpl;
import ru.hse.lorddux.executors.PythonExecutor;
import ru.hse.lorddux.queue.DeleteQueueMessagesClient;
import ru.hse.lorddux.queue.GetQueueMessagesClient;
import ru.hse.lorddux.queue.QueueProcessor;
import ru.hse.lorddux.queue.QueueProcessorImpl;
import ru.hse.lorddux.transport.TCPTransport;
import ru.hse.lorddux.transport.TransportManager;
import ru.hse.lorddux.utils.download.Downloader;
import ru.hse.lorddux.utils.download.GitDownloader;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

/**
 * the main class if the worker
 * obtains configuration.
 * starts up executors, queue processors
 */
@NoArgsConstructor
public class Adapter implements Service {
    private static Logger log_ = LogManager.getLogger(Adapter.class);
    private static final String DEFAULT_SUBDIRECTORY = "var";

    private Collection<PythonExecutor> executors;
    private GetQueueMessagesClient getMessagesClient;
    private DeleteQueueMessagesClient deleteMessagesClient;
    private StorageLayerConnector storageLayerConnector;
    private Thread getMessagesClientThread;
    private Thread deleteMessagesClint;
    private Thread storageLayerConnectorThread;
    private Downloader downloader;
    private boolean runningFlag = false;

    @Override
    synchronized public void start() {
        log_.info("Initialize services");
        try {
            init();
        } catch (Exception e) {
            log_.fatal("Can not init services", e);
            System.exit(1);
            return;
        }

        log_.info("Starting services");

        log_.info("Starting executors");
        executors.parallelStream().forEach(Thread::start);

        log_.info("Starting GetMessagesQueueClient");
        getMessagesClientThread.start();

        log_.info("Starting DeleteMessagesQueueClient");
        deleteMessagesClint.start();

        log_.info("Starting StorageLayerConnector");
        storageLayerConnectorThread.start();
        runningFlag = true;
        log_.info("All services were successfully started");
    }

    @Override
    public boolean isRunning() {
        return runningFlag;
    }

    @Override
    synchronized public void stop() {
        log_.info("Stopping services");
        getMessagesClient.stop();
        executors.parallelStream().forEach(PythonExecutor::stopThread);
        deleteMessagesClient.stop();
        storageLayerConnector.stop();

        executors.parallelStream().forEach(this::joinThread);
        joinThread(getMessagesClientThread);
        joinThread(deleteMessagesClint);
        joinThread(storageLayerConnectorThread);
        runningFlag = false;
        log_.info("All services were successfully stopped");
    }

    private void init() throws Exception {
        Configuration configuration = Configuration.getInstance();
        executors = new LinkedList<>();

        log_.info("Initializing executors");
        for (int i = 0; i < configuration.getWorkerCapacity(); i++) {
            executors.add(new PythonExecutor(
                    configuration.getCodeConfig().getCommand(),
                    DEFAULT_SUBDIRECTORY + "/" + configuration.getCodeConfig().getMainFile(),
                    PythonExecutor.DEFAULT_QUEUE_SIZE
            ));
        }

        downloader = new GitDownloader();

        log_.info("Creating queue processor");
        QueueProcessor queueProcessor = new QueueProcessorImpl(
                configuration.getQueueConfig().getStorageConnectionString(),
                configuration.getQueueConfig().getQueueName()
        );

        log_.info("Creating GetQueueMessagesClient");
        getMessagesClient = new GetQueueMessagesClient(executors, queueProcessor);

        log_.info("Creating DeleteQueueMessagesClient");
        deleteMessagesClient = new DeleteQueueMessagesClient(executors, queueProcessor);

        log_.info("Creating StorageLayerConnector");
        storageLayerConnector = new StorageLayerConnectorImpl(executors, new TransportManager(
                new TCPTransport(
                        configuration.getStorageLayerConfig().getAddress(),
                        configuration.getStorageLayerConfig().getPort()
                )
        ));

        log_.info(String.format("Clearing '%s' directory", DEFAULT_SUBDIRECTORY));
        try {
            FileUtils.cleanDirectory(new File(DEFAULT_SUBDIRECTORY));
        } catch (Exception e) {
            log_.debug(e);
        }

        log_.info(String.format("Downloading code from %s", configuration.getCodeConfig().getAddress()));
        downloader.download(configuration.getCodeConfig().getAddress(), DEFAULT_SUBDIRECTORY);

        getMessagesClientThread = new Thread(getMessagesClient);
        deleteMessagesClint = new Thread(deleteMessagesClient);
        storageLayerConnectorThread = new Thread(storageLayerConnector);
    }

    private void joinThread(Thread thread) {
        try {
            thread.interrupt();
            thread.join(2000);
        } catch (InterruptedException e) {

        }
    }
}
