package ru.hse.lorddux.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import ru.hse.lorddux.structures.request.InitRequestData;

import java.net.URISyntaxException;

public class ConfigurationRequestService implements RequestService<InitRequestData> {
    InitRequestData requestData;
    URIBuilder uriBuilder;

    public ConfigurationRequestService(InitRequestData requestData, String host, String path) {
        uriBuilder = new URIBuilder().setScheme("http").setHost(host).setPath(path);
        this.requestData = requestData;
    }

    public HttpGet createRequest(InitRequestData requestData) throws URISyntaxException {
        uriBuilder.setParameter("cpu", this.requestData.getCpu().toString());
        uriBuilder.setParameter("ram", this.requestData.getRam().toString());
        uriBuilder.setParameter("hdd", this.requestData.getHdd().toString());
        uriBuilder.setParameter("ssd", this.requestData.getSsd().toString());
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        return httpGet;
    }
}
