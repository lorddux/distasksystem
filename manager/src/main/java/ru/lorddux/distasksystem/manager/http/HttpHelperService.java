package ru.lorddux.distasksystem.manager.http;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class HttpHelperService {
    private static final Logger log_ = LogManager.getLogger(HttpHelperService.class);
    private static final int DEFAULT_ATTEMPTS_COUNT = 3;

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

}