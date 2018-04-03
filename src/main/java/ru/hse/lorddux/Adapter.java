package ru.hse.lorddux;

import ru.hse.lorddux.config.Configuration;
import ru.hse.lorddux.executor.PythonExecutor;
import ru.hse.lorddux.http.ConfigurationRequest;
import ru.hse.lorddux.structures.request.InitRequestData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * the main class if the worker
 * obtains configuration.
 * starts up executors, queue processors
 */
public class Adapter {

    private List<PythonExecutor> executors;
    private String configServerHost;
    private String configPath;

    Adapter(){

    }

    public void execute() {

        InitRequestData requestData = new InitRequestData();
        ConfigurationRequest request = new ConfigurationRequest(requestData, configServerHost, configPath);
    }

    public static void main(String[] args) throws Exception {
        PythonExecutor e = new PythonExecutor("python", "C:\\test.py", 10);
        System.out.println(e.processTask("kk"));
    }
}
