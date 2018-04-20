package ru.lorddux.distasksystem.storage.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import ru.lorddux.distasksystem.storage.data.request.PCParametersData;

import java.net.URISyntaxException;

public class ConfigurationRequestCreator implements RequestCreator<Integer> {
    URIBuilder uriBuilder;

    public ConfigurationRequestCreator(String host, String path) {
        uriBuilder = new URIBuilder().setScheme("http").setHost(host).setPath(path);
    }

    public HttpGet createRequest(Integer port) throws URISyntaxException {
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.setHeader("x-type", "STORAGE");
        if (port != null) {
            httpGet.setHeader("x-port", port.toString());
        }
        return httpGet;
    }
}
