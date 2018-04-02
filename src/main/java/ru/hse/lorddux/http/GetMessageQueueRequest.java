package ru.hse.lorddux.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import ru.hse.lorddux.structures.request.GetMessageQueueRequestData;
import ru.hse.lorddux.structures.response.TaskResponseData;

import java.net.URISyntaxException;

public class GetMessageQueueRequest extends Request<String, TaskResponseData> {

    private String authorization;
    private GetMessageQueueRequestData requestData;

    public GetMessageQueueRequest(GetMessageQueueRequestData requestData, String authorization, String host, String path) {
        super(host, path, TaskResponseData.class);
        this.requestData = requestData;
        this.authorization = authorization;
    }

    protected HttpUriRequest createRequest(URIBuilder uriBuilder) throws URISyntaxException {
        uriBuilder.setParameter("numofmessages", this.requestData.getNumberOfMessages().toString());
        uriBuilder.setParameter("visibilitytimeout", this.requestData.getVisibilityTimeout().toString());
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.setHeader("Authorization", this.authorization);
        httpGet.setHeader("x-ms-date", String.valueOf(System.currentTimeMillis() / 1000));
        return httpGet;
    }
}
