package ru.lorddux.distasksystem.worker;

import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.worker.config.Configuration;
import ru.lorddux.distasksystem.worker.connector.StorageLayerConnector;
import ru.lorddux.distasksystem.worker.connector.StorageLayerConnectorImpl;
import ru.lorddux.distasksystem.worker.executors.CommandExecutor;
import ru.lorddux.distasksystem.worker.executors.ExecutorImpl;
import ru.lorddux.distasksystem.worker.executors.PythonExecutor;
import ru.lorddux.distasksystem.worker.queue.DeleteQueueMessagesClient;
import ru.lorddux.distasksystem.worker.queue.GetQueueMessagesClient;
import ru.lorddux.distasksystem.worker.queue.QueueProcessor;
import ru.lorddux.distasksystem.worker.queue.QueueProcessorImpl;
import ru.lorddux.distasksystem.worker.transport.TCPTransport;
import ru.lorddux.distasksystem.worker.transport.TransportManager;
import ru.lorddux.distasksystem.worker.utils.PipInstaller;
import ru.lorddux.distasksystem.worker.utils.download.Downloader;
import ru.lorddux.distasksystem.worker.utils.download.GitDownloader;

import java.io.File;
import java.io.IOException;
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

    private Collection<ExecutorImpl> executors;
    private GetQueueMessagesClient getMessagesClient;
    private DeleteQueueMessagesClient deleteMessagesClient;
    private StorageLayerConnector storageLayerConnector;
    private Thread getMessagesClientThread;
    private Thread deleteMessagesClint;
    private Thread storageLayerConnectorThread;
    private Downloader downloader;
    private volatile boolean runningFlag = false;

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
        executors.parallelStream().forEach(ExecutorImpl::stopThread);
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
            if (configuration.getCodeConfig().getMainFile() != null) {
                executors.add(new PythonExecutor(
                        configuration.getCodeConfig().getCommand(),
                        DEFAULT_SUBDIRECTORY + "/" + configuration.getCodeConfig().getMainFile(),
                        ExecutorImpl.DEFAULT_QUEUE_SIZE
                ));
            } else {
                executors.add(new CommandExecutor(
                        configuration.getCodeConfig().getCommand(),
                        ExecutorImpl.DEFAULT_QUEUE_SIZE
                ));
            }
        }

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

        downloader = new GitDownloader();
        log_.info(String.format("Downloading code from %s", configuration.getCodeConfig().getAddress()));
        downloader.download(configuration.getCodeConfig().getAddress(), DEFAULT_SUBDIRECTORY);

        log_.info("Installing requirements");
        try {
            PipInstaller.installRequirements(DEFAULT_SUBDIRECTORY + "/requirements.txt");
        } catch (IOException e) {
            log_.info(String.format("Can not install requirements: %s", e.getMessage()));
        }
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
