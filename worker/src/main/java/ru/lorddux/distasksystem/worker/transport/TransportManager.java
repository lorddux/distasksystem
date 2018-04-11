package ru.lorddux.distasksystem.worker.transport;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@RequiredArgsConstructor
public class TransportManager {
    private static final Logger log_ = LogManager.getLogger(TransportManager.class);
    private static long SLEEP_RETRY_TIME = 3000;
    private volatile boolean stop = false;

    @NonNull
    private Transport transport;

    public void openConnection(int retriesNumber) throws IOException {
        log_.info(String.format("Connecting to %s:%d", transport.getAddress(), transport.getPort()));
        boolean opened = false;
        int retriesCount = 0;
        while (! opened && ! stop) {
            try {
                transport.connect();
                opened = true;
            } catch (IOException e) {
                if (retriesNumber >= 0 && retriesCount >= retriesNumber) {
                    throw e;
                }
                ++retriesCount;
                log_.warn(String.format("Can not connect to %s:%d. Retrying %d",
                        transport.getAddress(), transport.getPort(), retriesCount), e);
                try {
                    Thread.sleep(SLEEP_RETRY_TIME);
                } catch (InterruptedException ie) {
                    return;
                }
            }
        }
        log_.info(String.format("Successfully connected to %s:%d", transport.getAddress(), transport.getPort()));
    }

    public void stop() {
        stop = true;
    }

    public void closeConnection() throws IOException {
        transport.disconnect();
    }

    public void reopenConnection() throws IOException {
        try {
            closeConnection();
        } catch (IOException e) {
            log_.debug("Connection is closed", e);
        }
        openConnection(0);
    }

    public void sendMessageLine(String message) throws IOException {
        boolean sent = false;
        while (! sent) {
            try {
                transport.sendData(message + '\n');
                sent = true;
            } catch (IOException e) {
                reopenConnection();
            }
        }
    }

    public void sendMessage(String message) throws IOException {
        transport.sendData(message);
    }

    public void sendMessage(byte[] message) throws IOException {
        transport.sendData(message);
    }
}
