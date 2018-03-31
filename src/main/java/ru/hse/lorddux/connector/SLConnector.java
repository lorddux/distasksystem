package ru.hse.lorddux.connector;

import java.net.Socket;

public interface SLConnector {
    void openConnection(String address, int port);
}
