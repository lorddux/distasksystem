package ru.lorddux.distasksystem.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;


@Service
@RequiredArgsConstructor
public class HttpHelperService {

    private final SerializeService serializer;
    private final CloseableHttpClient httpClient;

    private static final int DEFAULT_ATTEMPTS_COUNT = 3;
    private final Logger logger = LogManager.getLogger();

    public <T> StringEntity createStringEntity(T object) throws JsonProcessingException {
        return new StringEntity(serializer.serialize(object), ContentType.APPLICATION_JSON);
    }

    public HttpPost postRequest(String url, HttpEntity data) {
        HttpPost post = new HttpPost(url);
        post.setEntity(data);
        return post;
    }

    public <T> HttpPost postJsonRequest(String url, T object) throws JsonProcessingException {
        return postRequest(url, createStringEntity(object));
    }

    public String sendRequest(HttpUriRequest request) throws IOException {
        logger.trace("sending http request " + request.getMethod() + " " + request.getURI());
        // try to send several times to deal with network problems
        IOException exception = null;
        for (int i = 0; i < DEFAULT_ATTEMPTS_COUNT; ++i) {
            try {
                try (CloseableHttpResponse response = httpClient.execute(request)) {

                    StatusLine line = response.getStatusLine();
                    if (line.getStatusCode() != HttpStatus.SC_OK)
                        throw new IOException(line.toString());

                    String responseData = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
                    logger.trace("request executed - " + responseData);
                    return responseData;
                }
            } catch (IOException ex) {
                logger.warn("sending http request problem - " + request.getURI(), ex);
                exception = ex;
            }
        }
        throw exception;
    }

    public <T> String sendPostJsonRequest(String url, T object) throws IOException {
        return sendRequest(postJsonRequest(url, object));
    }

    public String sendGetRequest(String url) throws IOException {
        return sendRequest(new HttpGet(url));
    }
}