package ru.lorddux.distasksystem.storage.http;

import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@AllArgsConstructor
public class HttpHelperService {
    private static final Logger log_ = LogManager.getLogger(HttpHelperService.class);
    private static final int DEFAULT_ATTEMPTS_COUNT = 5;

    private static volatile HttpHelperService instance;
    private final CloseableHttpClient httpClient;

    public String sendRequest(HttpUriRequest request) throws IOException {
        log_.trace("sending http request " + request.getMethod() + " " + request.getURI());
        // try to send several times to deal with network problems
        IOException exception = null;
        for (int i = 0; i < DEFAULT_ATTEMPTS_COUNT; ++i) {
            try {
                try (CloseableHttpResponse response = httpClient.execute(request)) {

                    StatusLine line = response.getStatusLine();
                    if (line.getStatusCode() != HttpStatus.SC_OK)
                        throw new IOException(line.toString());

                    String responseData = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
                    log_.trace("request executed - " + responseData);
                    return responseData;
                }
            } catch (IOException ex) {
                log_.warn("sending http request problem - " + request.getURI() + ": " + ex.getMessage());
                exception = ex;
            }
        }
        throw exception;
    }

    public static HttpHelperService getInstance() {
        HttpHelperService localInstance = instance;
        if (localInstance == null) {
            synchronized (HttpHelperService.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new HttpHelperService(HttpClients.createDefault());
                }
            }
        }
        return localInstance;
    }
}