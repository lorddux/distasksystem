package ru.lorddux.distasksystem.storage.receiver;

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

@RequiredArgsConstructor
public class SocketWorker implements Stopable {
    private static final Logger log_ = LogManager.getLogger(SocketWorker.class);
    private static int SO_TIMEOUT = 5000;
    private static int DISCONNECT_TIMEOUT_MILLIS = 10 * 60 * 1000; // 10 minutes

    @NonNull
    private Socket clientSocket;

    @NonNull
    private SentenceProcessor processor;

//    @NonNull
//    private DynamicQueuePool<WorkerTaskResult> destinationPool;

    private volatile boolean stopFlag = false;
    private ArrayBlockingQueue<WorkerTaskResult> destination;

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
        log_.info(String.format("Closing sql from %s", clientSocket.getInetAddress()));
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
