package ru.hse.lorddux;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.hse.lorddux.config.Configuration;
import ru.hse.lorddux.connector.StorageLayerConnector;
import ru.hse.lorddux.connector.StorageLayerConnectorImpl;
import ru.hse.lorddux.executor.PythonExecutor;
import ru.hse.lorddux.queue.DeleteQueueMessagesClient;
import ru.hse.lorddux.queue.GetQueueMessagesClient;
import ru.hse.lorddux.queue.QueueProcessor;
import ru.hse.lorddux.queue.QueueProcessorImpl;
import ru.hse.lorddux.transport.TCPTransport;
import ru.hse.lorddux.transport.TransportManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.LinkedList;

/**
 * the main class if the worker
 * obtains configuration.
 * starts up executors, queue processors
 */
@RequiredArgsConstructor
public class Adapter implements Service {
    private static Logger log_ = LogManager.getLogger(Adapter.class);

    @NonNull private Configuration configuration;
    private Collection<PythonExecutor> executors;
    private GetQueueMessagesClient getMessagesClient;
    private DeleteQueueMessagesClient deleteMessagesClient;
    private StorageLayerConnector storageLayerConnector;
    private Thread getMessagesClientThread;
    private Thread deleteMessagesClint;
    private Thread storageLayerConnectorThread;

    @Override
    public void start() {
        try {
            init();
        } catch (Exception e) {
            log_.fatal("Can not init resources", e);
            System.exit(1);
            return;
        }

        executors.parallelStream().forEach(Thread::start);
        getMessagesClientThread.start();
        deleteMessagesClint.start();
        storageLayerConnectorThread.start();
    }

    @Override
    public void stop() {
        getMessagesClient.stop();
        executors.parallelStream().forEach(PythonExecutor::stopThread);
        deleteMessagesClient.stop();
        storageLayerConnector.stop();

        executors.parallelStream().forEach(this::joinThread);
        joinThread(getMessagesClientThread);
        joinThread(deleteMessagesClint);
        joinThread(storageLayerConnectorThread);
    }

    private void init() throws Exception {
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
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(8765), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                System.out.println("KEK");
            }
        });
        server.start();
    }
}
