package ru.hse.lorddux.transport;

import java.io.IOException;
import java.util.Collection;

public interface Transport {
    void connect() throws IOException;
    void disconnect() throws IOException;
    void reconnect() throws IOException;
    void sendData(String data) throws IOException;
}
