package ru.hse.lorddux;

import lombok.NoArgsConstructor;
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

    private Collection<PythonExecutor> executors;
    private GetQueueMessagesClient getMessagesClient;
    private DeleteQueueMessagesClient deleteMessagesClient;
    private StorageLayerConnector storageLayerConnector;
    private Thread getMessagesClientThread;
    private Thread deleteMessagesClint;
    private Thread storageLayerConnectorThread;
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
        executors.parallelStream().forEach(Thread::start);
        getMessagesClientThread.start();
        deleteMessagesClint.start();
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
        for (int i = 0; i < configuration.getWorkerCapacity(); i++) {
            executors.add(new PythonExecutor(
                    configuration.getCodeConfig().getCommand(),
                    configuration.getCodeConfig().getMainFile(),
                    PythonExecutor.DEFAULT_QUEUE_SIZE
            ));
        }
        QueueProcessor queueProcessor = new QueueProcessorImpl(
                configuration.getQueueConfig().getStorageConnectionString(),
                configuration.getQueueConfig().getQueueName()
        );
        getMessagesClient = new GetQueueMessagesClient(executors, queueProcessor);
        deleteMessagesClient = new DeleteQueueMessagesClient(executors, queueProcessor);
        storageLayerConnector = new StorageLayerConnectorImpl(executors, new TransportManager(
                new TCPTransport(
                        configuration.getStorageLayerConfig().getAddress(),
                        configuration.getStorageLayerConfig().getPort()
                )
        ));

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

    public static void main(String[] args) throws Exception {

    }
}
