package ru.lorddux.distasksystem.worker.connector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.worker.executors.Executor;
import ru.lorddux.distasksystem.worker.transport.TransportManager;
import ru.lorddux.distasksystem.worker.utils.ExecutorQueuePool;
import ru.lorddux.distasksystem.worker.utils.QueuePool;

import java.io.IOException;
import java.util.Collection;

public class StorageLayerConnectorImpl implements StorageLayerConnector, Runnable {
    private static Logger log_ = LogManager.getLogger(StorageLayerConnector.class);

    private TransportManager transportManager;
    private QueuePool<String> resultQueuePool;
    private volatile boolean stop = false;
    public StorageLayerConnectorImpl(Collection<Executor> executors, TransportManager transportManager) {
        this.transportManager = transportManager;
        this.resultQueuePool = new ExecutorQueuePool<>(executors, Executor::getResultQueue);
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
            return;
        }
        while (!stop) {
            String result = resultQueuePool.poll(1000L);
            log_.trace(String.format("Sending the result to StorageLayer: %s", result));
            boolean sent = false;
            int tries = 0;
            while (!sent) {
                tries++;
                try {
                    transportManager.sendMessageLine(result);
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
}
