package ru.hse.lorddux.http;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import ru.hse.lorddux.structures.request.DeleteMessageQueueRequestData;
import ru.hse.lorddux.structures.response.TaskResponseData;

import java.net.URISyntaxException;

public class DeleteMessageQueueRequest extends Request<String, TaskResponseData> {

    private String authorization;
    private DeleteMessageQueueRequestData requestData;

    public DeleteMessageQueueRequest(DeleteMessageQueueRequestData requestData, String authorization, String host, String path) {
        super(host, path, TaskResponseData.class);
        this.requestData = requestData;
        this.authorization = authorization;
    }

    protected HttpUriRequest createRequest(URIBuilder uriBuilder) throws URISyntaxException {
        uriBuilder.setParameter("popreceipt", this.requestData.getPopReceipt());
        HttpDelete httpDelete = new HttpDelete(uriBuilder.build());
        httpDelete.setHeader("Authorization", this.authorization);
        httpDelete.setHeader("x-ms-date", String.valueOf(System.currentTimeMillis() / 1000));
        return httpDelete;
    }
}