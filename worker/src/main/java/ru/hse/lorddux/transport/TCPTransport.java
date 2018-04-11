package ru.hse.lorddux.transport;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.net.Socket;

@RequiredArgsConstructor
public class TCPTransport implements Transport {

    @Getter
    @NonNull
    private String address;

    @Getter
    @NonNull
    private Integer port;

    private Socket clientSocket;
    private DataOutputStream outputStreamToServer;
    private BufferedReader fromServer;

    @Override
    public void connect() throws IOException {
        clientSocket = new Socket(address, port);
        outputStreamToServer = new DataOutputStream(clientSocket.getOutputStream());
        fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void disconnect() throws IOException {
        if (clientSocket == null) return;
        clientSocket.shutdownOutput();
        clientSocket.shutdownInput();
        clientSocket.close();
    }

    @Override
    public void reconnect() throws IOException {
        disconnect();
        connect();
    }

    @Override
    public void sendData(String data) throws IOException {
        outputStreamToServer.writeBytes(data);
    }

    @Override
    public void sendData(byte[] data) throws IOException {
        outputStreamToServer.write(data);
    }

    @Override
    public String receiveData() throws IOException {
        return fromServer.readLine();
    }
}
