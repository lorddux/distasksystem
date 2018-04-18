package ru.lorddux.distasksystem.manager.http;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;

public class NodeCommandRequestCreator implements RequestCreator<String> {
    private URIBuilder uriBuilder;
    private String authorization;

    public NodeCommandRequestCreator(String host, String authorization) {
        uriBuilder = new URIBuilder().setScheme("http").setHost(host);
        this.authorization = authorization;
    }

    public HttpPost createRequest(String commandPath) throws URISyntaxException {
        uriBuilder.setPath(commandPath);
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        httpPost.setHeader("authorization", authorization);
        return httpPost;
    }
}
