package ru.lorddux.distasksystem.manager.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;
import java.util.Objects;

public class StatNodeRequestCreator {
    private static final String PATH = "/stat";
    private URIBuilder uriBuilder;
    private String authorization;

    public StatNodeRequestCreator(String host, String authorization) {
        uriBuilder = new URIBuilder().setScheme("http").setHost(host).setPath(PATH);
        this.authorization = authorization;
    }

    public HttpGet createRequest(Integer duration) throws URISyntaxException {
        uriBuilder.setParameter("duration", Objects.toString(duration, ""));
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.setHeader("authorization", authorization);
        return httpGet;
    }
}
