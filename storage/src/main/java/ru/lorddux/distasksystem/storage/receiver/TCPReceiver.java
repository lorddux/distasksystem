package ru.lorddux.distasksystem.storage.receiver;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.Stopable;

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

    @Override
    public void stop() {
        stopFlag = true;
    }

    @Override
    public void run() {
        try {
            createSocket();
        } catch (IOException e) {
            log_.error("Can not create socket", e);
            return;
        }
        while (! stopFlag) {
            try {
                Socket connectionSocket = socket.accept();
                log_.info("Client connected from " + connectionSocket.getInetAddress());
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
}
