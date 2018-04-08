package ru.hse.lorddux.config;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.hse.lorddux.http.ConfigurationRequestCreator;
import ru.hse.lorddux.http.HttpHelperService;
import ru.hse.lorddux.http.RequestCreator;
import ru.hse.lorddux.data.request.PCPropertiesData;

import java.io.IOException;
import java.net.URISyntaxException;

@RequiredArgsConstructor
public class ConfigurationClient implements Runnable {
    private static final Logger log_ = LogManager.getLogger(ConfigurationClient.class);
    private static long DEFAULT_SLEEP_TIME = 10000L;
    @NonNull private String configServerHost;
    @NonNull private String configPath;
    @NonNull private PCPropertiesData requestData;

    @Setter
    @Getter
    private long sleepTime = DEFAULT_SLEEP_TIME;
    private volatile boolean stop = false;

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        RequestCreator<PCPropertiesData> requestService = new ConfigurationRequestCreator(requestData, configServerHost, configPath);
        HttpUriRequest request;
        try {
            request = requestService.createRequest(requestData);
        } catch (URISyntaxException e) {
            log_.fatal("Can not get configuration. Exiting", e);
            System.exit(1);
            return;
        }

        log_.info(String.format("Constructed request - %s", request));
        Gson gson = new Gson();
        boolean obtained = false;
        long attemptsCount = 0;
        while (!stop && !obtained) {
            try {
                attemptsCount++;
                String configRaw = HttpHelperService.getInstance().sendRequest(request);
                log_.debug(String.format("Configuration raw: %s", configRaw));
                Configuration configuration = gson.fromJson(configRaw, Configuration.class);
                Configuration.setInstance(configuration);
                obtained = true;
                log_.info(String.format("Worker configuration: %s", configuration));
            } catch (IOException e) {
                if (attemptsCount % 10 == 1)
                    log_.warn(String.format("Can not get configuration due to exception: '%s'. Retrying #%s", e.toString(), attemptsCount));
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    log_.debug("Unexpected thread interruption");
                }
            }
        }
    }
}
