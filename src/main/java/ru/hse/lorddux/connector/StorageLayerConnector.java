package ru.hse.lorddux.connector;


import java.io.IOException;
import java.util.Collection;

public interface StorageLayerConnector {
    void connect() throws IOException;
    void disconnect() throws IOException;
    void reconnect() throws IOException;
    void sendData(String data) throws IOException;
    void sendData(Collection<String> dataCollection) throws IOException;
}
