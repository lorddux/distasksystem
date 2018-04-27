package ru.lorddux.distasksystem.storage.http;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;

public class ConfigurationRequestCreator implements RequestCreator<Integer> {
    private URIBuilder uriBuilder;
    private String address;

    public ConfigurationRequestCreator(String host, String path, String address) {
        uriBuilder = new URIBuilder().setScheme("http").setHost(host).setPath(path);
        this.address = address;
    }

    public HttpPost createRequest(Integer port) throws URISyntaxException {
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        httpPost.setHeader("x-type", "STORAGE");
        if (port != null) {
            httpPost.setHeader("x-port", port.toString());
        }

        if (address != null) {
            httpPost.setHeader("x-addr", address);
        }
        return httpPost;
    }
}
