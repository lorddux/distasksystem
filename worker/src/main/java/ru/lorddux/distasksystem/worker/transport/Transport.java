package ru.lorddux.distasksystem.worker.transport;

import java.io.IOException;
import java.util.Collection;

public interface Transport {
    void connect() throws IOException;
    void disconnect() throws IOException;
    void reconnect() throws IOException;
    void sendData(String data) throws IOException;
    void sendData(byte[] data) throws IOException;
    String receiveData() throws IOException;
    String getAddress();
    Integer getPort();
}
