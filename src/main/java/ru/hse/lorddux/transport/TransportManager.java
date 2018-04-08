package ru.hse.lorddux.transport;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@RequiredArgsConstructor
public class TransportManager {
    private static final Logger log_ = LogManager.getLogger(TransportManager.class);
    private static long SLEEP_RETRY_TIME = 5000;
    private volatile boolean stop = false;

    @NonNull
    private Transport transport;

    public void openConnection(int retriesNumber) throws IOException {
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
    }

    public void stop() {
        stop = true;
    }

    public void closeConnection() throws IOException {
        transport.disconnect();
    }

    public void reopenConnection() throws IOException {
        closeConnection();
        openConnection(0);
    }

    public void sendMessageLine(String message) throws IOException {
        transport.sendData(message + '\n');
    }

    public void sendMessage(String message) throws IOException {
        transport.sendData(message);
    }

    public void sendMessage(byte[] message) throws IOException {
        transport.sendData(message);
    }
}
