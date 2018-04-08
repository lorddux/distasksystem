package ru.hse.lorddux.connector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.hse.lorddux.executor.PythonExecutor;
import ru.hse.lorddux.transport.TCPTransport;
import ru.hse.lorddux.transport.TransportManager;
import ru.hse.lorddux.utils.ExecutorQueuePool;
import ru.hse.lorddux.utils.QueuePool;

import java.io.IOException;
import java.util.Collection;

public class StorageLayerConnectorImpl implements StorageLayerConnector, Runnable {
    private static Logger log_ = LogManager.getLogger(StorageLayerConnector.class);

    private TransportManager transportManager;
    private QueuePool<String> resultQueuePool;
    private volatile boolean stop = false;
    public StorageLayerConnectorImpl(Collection<PythonExecutor> executors, TransportManager transportManager) {
        this.transportManager = transportManager;
        this.resultQueuePool = new ExecutorQueuePool<>(executors, PythonExecutor::getResultQueue);
    }

    public void stop() {
        stop = true;
        transportManager.stop();
    }

    @Override
    public void run() {
        try {
            transportManager.openConnection(-1);
        } catch (IOException e) {
            log_.fatal("Could not open connection. Exiting", e);
            System.exit(1);
            return;
        }
        while (!stop) {
            String result = resultQueuePool.poll(1000L);
            boolean sent = false;
            int tries = 0;
            while (!sent) {
                tries++;
                try {
                    transportManager.sendMessage(result);
                    sent = true;
                } catch (IOException e) {
                    log_.warn(String.format("Can not send result. Retrying #%s", tries), e);
                }
            }
        }
        try {
            transportManager.closeConnection();
        } catch (IOException e) {
            log_.error(e);
        }
    }

    public static void main(String[] args) throws Exception {
        TransportManager m = new TransportManager(new TCPTransport("192.168.29.23", 1234));
        Thread k = new Thread(() -> {try {
            m.openConnection(-1);
        } catch (IOException e) {
        }
        });

        k.start();
        m.stop();
        k.join();
    }
}
