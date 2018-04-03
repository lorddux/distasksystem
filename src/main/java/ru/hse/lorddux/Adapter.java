package ru.hse.lorddux;

import com.google.gson.Gson;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.hse.lorddux.config.Configuration;
import ru.hse.lorddux.executor.PythonExecutor;
import ru.hse.lorddux.http.ConfigurationRequestService;
import ru.hse.lorddux.http.HttpHelperService;
import ru.hse.lorddux.http.RequestService;
import ru.hse.lorddux.structures.request.InitRequestData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * the main class if the worker
 * obtains configuration.
 * starts up executors, queue processors
 */
@RequiredArgsConstructor
public class Adapter implements Runnable{
    private static Logger log_ = LogManager.getLogger(Adapter.class);

    @NonNull private String configServerHost;
    @NonNull private String configPath;
    @NonNull private Long sleepTime;
    private volatile boolean stop = false;
    private List<PythonExecutor> executors;

    @Override
    public void run() {
        InitRequestData requestData = new InitRequestData();
        RequestService<InitRequestData> requestService = new ConfigurationRequestService(requestData, configServerHost, configPath);
        HttpUriRequest request;
        try {
            request = requestService.createRequest(requestData);
        } catch (URISyntaxException e) {
            log_.fatal("Can not get configuration", e);
            return;
        }

        boolean obtained = false;
        long attemptsCount = 0;
        while (!stop && !obtained) {
            try {
                attemptsCount++;
                Configuration configuration = new Gson().fromJson(
                        HttpHelperService.getInstance().sendRequest(request), Configuration.class
                );
                Configuration.setInstance(configuration);
                obtained = true;
            } catch (IOException e) {
                if (attemptsCount % 10 == 1)
                log_.warn(String.format("Trying to obtain configuration #%s", attemptsCount));
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {

                }
            }
        }
    }

    public void execute() {

        InitRequestData requestData = new InitRequestData();
        ConfigurationRequestService request = new ConfigurationRequestService(requestData, configServerHost, configPath);
    }

    public static void main(String[] args) throws Exception {
        PythonExecutor e = new PythonExecutor("python", "C:\\test.py", 10);
        System.out.println(e.processTask("kk"));
    }
}
