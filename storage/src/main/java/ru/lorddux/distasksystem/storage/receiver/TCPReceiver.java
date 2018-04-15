package ru.lorddux.distasksystem.storage.receiver;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.Stopable;
import ru.lorddux.distasksystem.storage.data.WorkerTaskResult;
import ru.lorddux.distasksystem.storage.receiver.processors.SentenceProcessor;
import ru.lorddux.distasksystem.utils.DynamicQueuePool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

@RequiredArgsConstructor
public class TCPReceiver implements Stopable {
    private static final Logger log_ = LogManager.getLogger(TCPReceiver.class);
    private static final int SOCKET_TIMEOUT = 5000;
    private static final int BUFFER_SIZE = 1 << 15;
    private ServerSocket socket;
    private volatile boolean stopFlag = false;

    @NonNull
    private Integer port;

    @NonNull
    private SentenceProcessor processor;

    @NonNull
    private DynamicQueuePool<WorkerTaskResult> pool;

    @Override
    public void stop() {
        stopFlag = true;
    }

    @Override
    public void run() {
        log_.info("run()");
        try {
            createSocket();
        } catch (IOException e) {
            log_.error("Can not create socket", e);
            return;
        }
        log_.info("Listen port " + port);
        while (! stopFlag) {
            try {
                Socket connectionSocket = socket.accept();
                log_.info("Client connected from " + connectionSocket.getInetAddress());
                new SocketWorkerThread(
                        new SocketWorker(connectionSocket, processor),
                        pool
                ).start();

            } catch (SocketTimeoutException ex) {
                log_.debug("Socket timeout: " + ex.getMessage());
            } catch (IOException ex) {
                log_.error(String.format("Socket error: %s", ex.getMessage()));
            } catch (Exception ex) {
                log_.warn(String.format("Unexpected error: %s", ex.toString()));
            }
        }
    }

    private void createSocket() throws IOException {
            socket = new ServerSocket(port);
            socket.setSoTimeout(SOCKET_TIMEOUT);
            socket.setReceiveBufferSize(BUFFER_SIZE);
            if(socket.getReceiveBufferSize() != BUFFER_SIZE) {
                log_.warn("Unable to set buffer size to " + BUFFER_SIZE + ", actual buffer size is " + socket.getReceiveBufferSize());
            }
            log_.debug("Socket successfully binded.");
    }

    private class SocketWorkerThread extends Thread {
        private DynamicQueuePool<WorkerTaskResult> pool;
        private SocketWorker worker;

        public SocketWorkerThread(SocketWorker worker, DynamicQueuePool<WorkerTaskResult> pool) {
            this.worker = worker;
            this.pool = pool;
            this.pool.addQueue(this.worker.getDestination());
        }

        @Override
        public void run() {
            worker.run();
            this.pool.removeQueue(this.worker.getDestination());
        }
    }
}
