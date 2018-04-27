package ru.lorddux.distasksystem.storage.receiver;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.Stopable;
import ru.lorddux.distasksystem.storage.data.WorkerTaskResult;
import ru.lorddux.distasksystem.storage.receiver.processors.SentenceProcessor;
import ru.lorddux.distasksystem.utils.DynamicQueuePool;
import ru.lorddux.distasksystem.utils.QueuePool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor
public class SocketWorker implements Stopable {
    private static final Logger log_ = LogManager.getLogger(SocketWorker.class);
    private static final int SO_TIMEOUT = 5000;
    private static final int DISCONNECT_TIMEOUT_MILLIS = 10 * 60 * 1000; // 10 minutes
    private static final int QUEUE_SIZE = 1000;

    @NonNull
    private Socket clientSocket;

    @NonNull
    private SentenceProcessor processor;

    @Getter
    private BlockingQueue<WorkerTaskResult> destination = new ArrayBlockingQueue<>(QUEUE_SIZE);

    private volatile boolean stopFlag = false;

    @Override
    public void stop() {
        stopFlag = true;
    }

    @Override
    public void run() {
        try {
            clientSocket.setSoTimeout(SO_TIMEOUT);
        } catch (SocketException e) {
            log_.fatal("Can not set socket timeout", e);
            return;
        }

        BufferedReader inFromClient;
        try {
            InputStreamReader reader = new InputStreamReader(clientSocket.getInputStream());
            inFromClient = new BufferedReader(reader);
        } catch (IOException e) {
            log_.fatal("Can not get input stream from socket");
            return;
        }
        String clientSentence;
        boolean read = false;
        long startIdleTime = System.currentTimeMillis();
        while (! read && ! stopFlag) {
            try {
                while ((clientSentence = inFromClient.readLine()) != null) {
                    log_.debug("run(): readLine from buffer");
                    dispatch(clientSentence);
                    startIdleTime = System.currentTimeMillis();
                }
                read = true;
                closeConnection();

            } catch (SocketTimeoutException ex) {
                    log_.debug(String.format("Socket timeout: %s", ex.getMessage()));
                if (System.currentTimeMillis() - startIdleTime > DISCONNECT_TIMEOUT_MILLIS) {
                    log_.info("Idle timeout. Closing socket");
                    closeConnection();
                    read = true;
                }
            } catch (Exception e) {
                log_.fatal("An error was occurred while reading client sentence", e);
                closeConnection();
                return;
            }
        }
    }

    private void closeConnection() {
        log_.info(String.format("Closing connection from %s", clientSocket.getInetAddress()));
        try {
            clientSocket.shutdownInput();
            clientSocket.shutdownOutput();
            clientSocket.close();
        } catch (IOException e) {
            log_.error("Can not close socket", e);
        }
        log_.info(String.format("Connection from %s closed", clientSocket.getInetAddress()));
    }

    private void dispatch(String sentence) {
        WorkerTaskResult result = processor.decode(sentence);
        while (! destination.offer(result)) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                log_.info("Thread was interrupted. Exiting");
            }
        }
    }
}
